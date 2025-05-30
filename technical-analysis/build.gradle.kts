import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("default-convention")
    alias(libs.plugins.kmm.publish)
    alias(libs.plugins.dokka)
}

dependencies {
    implementation(libs.multik.core)
    implementation(libs.multik.default)
    implementation(libs.dataframe)
}

group = "com.velkonost"
version = libs.versions.technical.analysis.get()
description = "Technical analysis popular indicators"

mavenPublishing {
    configure(
        KotlinJvm(
            sourcesJar = true,
            javadocJar = JavadocJar.Dokka("dokkaHtml"),
        )
    )

    coordinates(
        groupId = project.group.toString(),
        artifactId = "technical-analysis",
        version = libs.versions.technical.analysis.get()
    )

    pom {
        name.set("Technical Analysis Library")
        description.set(project.description)
        inceptionYear.set("2025")
        url.set("https://github.com/Velkonost/binance-sdk")

        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("velkonost")
                name.set("Artem Klimenko")
                email.set("velkonost@gmail.com")
                url.set("t.me/velkonost")
            }
        }

        scm {
            url.set("https://github.com/Velkonost/technical-analysis")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()
}