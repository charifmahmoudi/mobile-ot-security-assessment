#!/usr/bin/env python3
"""Deterministic, read-only Modbus identity endpoint for the pilot case harness."""
import socketserver
import struct


IDENTITY = ((0, b"Atlas CI Water Controls"), (1, b"Pump PLC Fixture"), (2, b"1.0"))


class Handler(socketserver.BaseRequestHandler):
    def handle(self):
        try:
            header = self._read_exact(7)
        except ConnectionError:
            print(f"READINESS_PROBE peer={self.client_address[0]}", flush=True)
            return
        transaction, protocol, length, unit = struct.unpack(">HHHB", header)
        payload = self._read_exact(length - 1)
        print(f"REQUEST peer={self.client_address[0]} unit={unit} pdu={payload.hex()}", flush=True)
        if protocol != 0 or len(payload) < 4 or payload[0:2] != b"\x2b\x0e":
            print("REJECT unsupported request", flush=True)
            return
        objects = b"".join(bytes((object_id, len(value))) + value for object_id, value in IDENTITY)
        response_pdu = b"\x2b\x0e\x01\x01\x00\x00" + bytes((len(IDENTITY),)) + objects
        response = struct.pack(">HHHB", transaction, 0, len(response_pdu) + 1, unit) + response_pdu
        self.request.sendall(response)
        print(f"RESPONSE unit={unit} bytes={len(response)}", flush=True)

    def _read_exact(self, size):
        result = bytearray()
        while len(result) < size:
            chunk = self.request.recv(size - len(result))
            if not chunk:
                raise ConnectionError("client disconnected")
            result.extend(chunk)
        return bytes(result)


class Server(socketserver.ThreadingTCPServer):
    allow_reuse_address = True
    daemon_threads = True


if __name__ == "__main__":
    with Server(("0.0.0.0", 502), Handler) as server:
        print("READY golden-ot-emulator tcp=502 target=10.0.2.2 unit=1 operation=modbus-device-id-basic", flush=True)
        server.serve_forever()
