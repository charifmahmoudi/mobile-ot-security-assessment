package com.atlasot.domain

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResearchPcapTest {
    private val root = File(requireNotNull(System.getenv("RESEARCH_PCAP_DIR")))

    @Test fun `ITI Modbus capture identifies Modbus assets`() = verify("modbus.pcap", OtProtocol.MODBUS_TCP)
    @Test fun `ITI DNP3 capture identifies DNP3 assets`() = verify("dnp3.pcap", OtProtocol.DNP3)
    @Test fun `ITI IEC104 capture identifies IEC104 assets`() = verify("iec104.pcap", OtProtocol.IEC_104)
    @Test fun `ITI BACnet capture identifies BACnet assets`() = verify("bacnet.pcap", OtProtocol.BACNET_IP)

    private fun verify(name: String, expected: OtProtocol) {
        val file = File(root, name)
        assertTrue(file.isFile, "research fixture missing: ${file.absolutePath}")
        val result = file.inputStream().use { PassivePcapAnalyzer.analyze(it) }
        assertTrue(result.totalPackets > 0)
        assertTrue(result.parsedPackets > 0, "no supported packets in $name")
        assertTrue(result.protocolCounts.getOrDefault(expected, 0) > 0, "missing $expected in $name")
        assertTrue(result.assets.any { expected in it.protocols && it.confidence >= 80 })
        assertEquals(64, result.sha256.length)
    }
}
