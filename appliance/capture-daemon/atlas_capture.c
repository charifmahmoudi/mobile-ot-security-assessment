#define _GNU_SOURCE
#include <arpa/inet.h>
#include <errno.h>
#include <fcntl.h>
#include <linux/if_ether.h>
#include <linux/if_packet.h>
#include <net/if.h>
#include <poll.h>
#include <signal.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <time.h>
#include <unistd.h>

#define SNAPLEN 262144U

static volatile sig_atomic_t stop_requested;
static void stop_capture(int signal_number) { (void)signal_number; stop_requested = 1; }

struct pcap_header {
    uint32_t magic;
    uint16_t major;
    uint16_t minor;
    int32_t zone;
    uint32_t sigfigs;
    uint32_t snaplen;
    uint32_t network;
};

struct pcap_record {
    uint32_t seconds;
    uint32_t micros;
    uint32_t captured;
    uint32_t original;
};

static int write_all(int fd, const void *data, size_t length) {
    const uint8_t *cursor = data;
    while (length > 0) {
        ssize_t count = write(fd, cursor, length);
        if (count < 0 && errno == EINTR) continue;
        if (count <= 0) return -1;
        cursor += count; length -= (size_t)count;
    }
    return 0;
}

static long long monotonic_ms(void) {
    struct timespec value;
    if (clock_gettime(CLOCK_MONOTONIC, &value) != 0) return -1;
    return (long long)value.tv_sec * 1000LL + value.tv_nsec / 1000000LL;
}

static void usage(const char *name) {
    fprintf(stderr, "usage: %s --interface IFACE --output FILE --max-bytes N --duration-ms N\n", name);
}

int main(int argc, char **argv) {
    const char *interface_name = NULL, *output_path = NULL;
    unsigned long long max_bytes = 0;
    long duration_ms = 0;
    for (int index = 1; index < argc; ++index) {
        if (!strcmp(argv[index], "--interface") && index + 1 < argc) interface_name = argv[++index];
        else if (!strcmp(argv[index], "--output") && index + 1 < argc) output_path = argv[++index];
        else if (!strcmp(argv[index], "--max-bytes") && index + 1 < argc) max_bytes = strtoull(argv[++index], NULL, 10);
        else if (!strcmp(argv[index], "--duration-ms") && index + 1 < argc) duration_ms = strtol(argv[++index], NULL, 10);
        else { usage(argv[0]); return 2; }
    }
    if (!interface_name || !output_path || max_bytes == 0 || max_bytes > 1024ULL * 1024 * 1024 ||
        duration_ms < 100 || duration_ms > 4L * 60 * 60 * 1000) {
        usage(argv[0]); return 2;
    }

    unsigned int interface_index = if_nametoindex(interface_name);
    if (interface_index == 0) { perror("if_nametoindex"); return 3; }
    int packet_fd = socket(AF_PACKET, SOCK_RAW | SOCK_CLOEXEC, htons(ETH_P_ALL));
    if (packet_fd < 0) { perror("AF_PACKET socket (CAP_NET_RAW required)"); return 4; }

    struct sockaddr_ll address = {0};
    address.sll_family = AF_PACKET;
    address.sll_protocol = htons(ETH_P_ALL);
    address.sll_ifindex = (int)interface_index;
    if (bind(packet_fd, (struct sockaddr *)&address, sizeof(address)) != 0) { perror("bind"); close(packet_fd); return 5; }

    struct packet_mreq membership = {0};
    membership.mr_ifindex = (int)interface_index;
    membership.mr_type = PACKET_MR_PROMISC;
    if (setsockopt(packet_fd, SOL_PACKET, PACKET_ADD_MEMBERSHIP, &membership, sizeof(membership)) != 0) {
        perror("PACKET_MR_PROMISC"); close(packet_fd); return 6;
    }

    int output_fd = open(output_path, O_WRONLY | O_CREAT | O_EXCL | O_CLOEXEC | O_NOFOLLOW, 0600);
    if (output_fd < 0) { perror("open output"); close(packet_fd); return 7; }
    const struct pcap_header header = {0xa1b2c3d4U, 2, 4, 0, 0, SNAPLEN, 1};
    if (write_all(output_fd, &header, sizeof(header)) != 0 || fsync(output_fd) != 0) {
        perror("publish header"); close(output_fd); close(packet_fd); return 8;
    }

    signal(SIGINT, stop_capture); signal(SIGTERM, stop_capture);
    uint8_t *buffer = malloc(SNAPLEN);
    if (!buffer) { close(output_fd); close(packet_fd); return 9; }
    unsigned long long bytes = 0, packets = 0, dropped_for_limit = 0;
    const long long deadline = monotonic_ms() + duration_ms;
    while (!stop_requested) {
        long long remaining = deadline - monotonic_ms();
        if (remaining <= 0 || bytes >= max_bytes) break;
        struct pollfd poll_descriptor = {.fd = packet_fd, .events = POLLIN};
        int ready = poll(&poll_descriptor, 1, remaining > 250 ? 250 : (int)remaining);
        if (ready < 0 && errno == EINTR) continue;
        if (ready < 0) { perror("poll"); break; }
        if (ready == 0 || !(poll_descriptor.revents & POLLIN)) continue;
        ssize_t received = recv(packet_fd, buffer, SNAPLEN, MSG_TRUNC);
        if (received < 0 && errno == EINTR) continue;
        if (received < 0) { perror("recv"); break; }
        uint32_t captured = (uint32_t)((received > SNAPLEN) ? SNAPLEN : received);
        if (bytes + captured > max_bytes) { ++dropped_for_limit; break; }
        struct timespec timestamp;
        clock_gettime(CLOCK_REALTIME, &timestamp);
        const struct pcap_record record = {(uint32_t)timestamp.tv_sec, (uint32_t)(timestamp.tv_nsec / 1000), captured, (uint32_t)received};
        if (write_all(output_fd, &record, sizeof(record)) || write_all(output_fd, buffer, captured)) { perror("write packet"); break; }
        bytes += captured; ++packets;
    }
    fsync(output_fd);
    free(buffer); close(output_fd); close(packet_fd);
    printf("{\"interface\":\"%s\",\"packets\":%llu,\"bytes\":%llu,\"limitDrops\":%llu}\n",
           interface_name, packets, bytes, dropped_for_limit);
    return packets > 0 ? 0 : 10;
}
