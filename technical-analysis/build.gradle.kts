import org.jreleaser.model.Active

plugins {
    id("default-convention")
    id("maven-publish")
    alias(libs.plugins.jreleaser)
}

dependencies {
    implementation(libs.multik.core)
    implementation(libs.multik.default)
    implementation(libs.dataframe)
}

java {
    withJavadocJar()
    withSourcesJar()
}


group = "velkonost.sdk"
version = libs.versions.technical.analysis.get()
description = "Technical analysis popular indicators"

publishing {

    publications {
        create<MavenPublication>("release") {
            from(components["java"])

            groupId = "velkonost.sdk"
            artifactId = "technical-analysis"

        }
    }

    repositories {
        maven {
            setUrl(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

jreleaser {
//    project {
//        inceptionYear = "2024"
//        author("@velkonost")
//    }
    release {
        github {
            skipRelease = true
            skipTag = true
            sign = true
            branch = "main"
            branchPush = "main"
            overwrite = true
        }
    }
//    signing {
//        active = Active.ALWAYS
//        armored = true
//        verify = true
//    }
//    deploy {
//        maven {
//            mavenCentral.create("sonatype") {
//                active = Active.ALWAYS
//                url = "https://central.sonatype.com/api/v1/publisher"
//                stagingRepository(layout.buildDirectory.dir("staging-deploy").get().toString())
//                setAuthorization("Basic")
//                retryDelay = 60
//            }
//        }
//    }
}