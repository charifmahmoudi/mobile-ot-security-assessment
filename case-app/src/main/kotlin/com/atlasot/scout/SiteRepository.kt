package com.atlasot.scout

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class SiteProfile(
    val id: String,
    val name: String,
    val location: String,
    val industry: String,
    val vendors: List<String>,
    val sample: Boolean = false,
)

data class InventoryAsset(
    val id: String,
    val siteId: String,
    val address: String,
    val displayName: String,
    val protocol: String,
    val role: String,
    val vendor: String?,
    val product: String?,
    val confidence: Int,
    val source: String,
    val evidence: String,
    val reviewState: String,
)

class SiteRepository(context: Context) {
    private val preferences = context.getSharedPreferences("atlas-sites-v1", Context.MODE_PRIVATE)

    fun sites(): List<SiteProfile> {
        val stored = preferences.getString("sites", null)
        if (stored == null) seed()
        return parseSites(preferences.getString("sites", "[]") ?: "[]")
    }

    fun addSite(name: String, location: String, industry: String, vendors: List<String>): SiteProfile {
        val site = SiteProfile(UUID.randomUUID().toString(), name.trim(), location.trim(), industry, vendors.sorted())
        writeSites(sites() + site)
        return site
    }

    fun assets(siteId: String): List<InventoryAsset> {
        sites()
        val array = JSONArray(preferences.getString("assets", "[]") ?: "[]")
        return buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                if (item.getString("siteId") == siteId) add(item.toAsset())
            }
        }
    }

    fun addAssets(siteId: String, additions: List<InventoryAsset>) {
        val existing = allAssets().toMutableList()
        additions.forEach { incoming ->
            val position = existing.indexOfFirst {
                it.siteId == siteId && it.address == incoming.address && it.protocol == incoming.protocol
            }
            if (position >= 0) existing[position] = incoming else existing += incoming
        }
        writeAssets(existing)
    }

    private fun allAssets(): List<InventoryAsset> {
        val array = JSONArray(preferences.getString("assets", "[]") ?: "[]")
        return buildList { for (index in 0 until array.length()) add(array.getJSONObject(index).toAsset()) }
    }

    private fun seed() {
        val site = SiteProfile(
            "sample-water", "North Water Treatment Plant", "Treatment line 2 · Casablanca",
            "Water & wastewater", listOf("Siemens", "Schneider Electric", "Endress+Hauser"), true,
        )
        writeSites(listOf(site))
        writeAssets(listOf(
            InventoryAsset("sample-plc", site.id, "10.20.10.11", "Intake PLC", "PROFINET", "Controller",
                "Siemens", "SIMATIC S7-1500", 96, "Imported baseline", "Approved inventory record", "Confirmed"),
            InventoryAsset("sample-gateway", site.id, "10.20.10.21", "Chlorination gateway", "Modbus/TCP", "Gateway",
                "Schneider Electric", "Modicon gateway", 92, "Passive capture", "Server responses on TCP/502", "Confirmed"),
            InventoryAsset("sample-hmi", site.id, "10.20.10.31", "Operator HMI", "Modbus/TCP", "HMI/client",
                null, null, 75, "Passive capture", "Client requests to two controller candidates", "Needs review"),
            InventoryAsset("sample-flow", site.id, "10.20.10.44", "Flow transmitter", "EtherNet/IP", "Field device",
                "Endress+Hauser", "Promag", 88, "Walkdown", "Nameplate and cabinet schedule", "Confirmed"),
        ))
    }

    private fun parseSites(value: String): List<SiteProfile> {
        val array = JSONArray(value)
        return buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                val vendors = item.getJSONArray("vendors")
                add(SiteProfile(
                    item.getString("id"), item.getString("name"), item.getString("location"),
                    item.getString("industry"), buildList { for (i in 0 until vendors.length()) add(vendors.getString(i)) },
                    item.optBoolean("sample", false),
                ))
            }
        }
    }

    private fun writeSites(sites: List<SiteProfile>) {
        val array = JSONArray()
        sites.forEach { site ->
            array.put(JSONObject().apply {
                put("id", site.id); put("name", site.name); put("location", site.location); put("industry", site.industry)
                put("vendors", JSONArray(site.vendors)); put("sample", site.sample)
            })
        }
        preferences.edit().putString("sites", array.toString()).apply()
    }

    private fun writeAssets(assets: List<InventoryAsset>) {
        val array = JSONArray()
        assets.forEach { asset ->
            array.put(JSONObject().apply {
                put("id", asset.id); put("siteId", asset.siteId); put("address", asset.address)
                put("displayName", asset.displayName); put("protocol", asset.protocol); put("role", asset.role)
                put("vendor", asset.vendor); put("product", asset.product); put("confidence", asset.confidence)
                put("source", asset.source); put("evidence", asset.evidence); put("reviewState", asset.reviewState)
            })
        }
        preferences.edit().putString("assets", array.toString()).apply()
    }

    private fun JSONObject.toAsset() = InventoryAsset(
        getString("id"), getString("siteId"), getString("address"), getString("displayName"), getString("protocol"),
        getString("role"), optString("vendor").takeIf { it.isNotBlank() && it != "null" },
        optString("product").takeIf { it.isNotBlank() && it != "null" }, getInt("confidence"), getString("source"),
        getString("evidence"), getString("reviewState"),
    )
}
