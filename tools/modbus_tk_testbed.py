#!/usr/bin/env python3
"""modbus-tk 1.1.5 slave simulator used to verify service-only identification."""

import signal
import time

import modbus_tk.defines as defines
import modbus_tk.modbus_tcp as modbus_tcp


server = modbus_tcp.TcpServer(address="0.0.0.0", port=502)
slave = server.add_slave(1)
slave.add_block("holding", defines.HOLDING_REGISTERS, 0, 32)
slave.set_values("holding", 0, [12, 18, 72, 64])
server.start()


def stop(*_args):
    server.stop()
    raise SystemExit(0)


signal.signal(signal.SIGTERM, stop)
signal.signal(signal.SIGINT, stop)
while True:
    time.sleep(1)
