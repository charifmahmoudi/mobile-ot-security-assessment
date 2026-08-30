plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation(kotlin("test-junit"))
}

tasks.test {
    useJUnit()
    environment("RESEARCH_PCAP_DIR", rootProject.file("testdata/research").absolutePath)
}
