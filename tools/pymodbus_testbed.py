#!/usr/bin/env python3
"""Pinned PyModbus 3.11.3 device-identification target for Android E2E CI."""

from pymodbus import ModbusDeviceIdentification
from pymodbus.datastore import (
    ModbusDeviceContext,
    ModbusSequentialDataBlock,
    ModbusServerContext,
)
from pymodbus.server import StartTcpServer


block = ModbusSequentialDataBlock(0, [0] * 100)
device = ModbusDeviceContext(di=block, co=block, hr=block, ir=block)
context = ModbusServerContext(devices=device, single=True)
identity = ModbusDeviceIdentification(
    info_name={
        "VendorName": "PyModbus",
        "ProductCode": "ATLAS-CI",
        "VendorUrl": "https://github.com/pymodbus-dev/pymodbus",
        "ProductName": "Water PLC Emulator",
        "ModelName": "P0-WATER",
        "MajorMinorRevision": "3.11.3",
    }
)

StartTcpServer(context=context, identity=identity, address=("0.0.0.0", 502))
