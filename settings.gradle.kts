/*
 *  Copyright (c) 2024 Huawei Technologies
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Huawei Technologies - initial API and implementation
 *
 */

// this is needed to have access to snapshot builds of plugins
pluginManagement {
    repositories {
        mavenLocal()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        mavenCentral()
    }
}

rootProject.name = "components"
include(":launchers:e2e-test")
include(":launchers:huawei-cloud-runtime")
include(":extensions")
include(":extensions:common:obs:obs-core")
include(":extensions:control-plane:provision-obs")
include(":extensions:data-plane:data-plane-obs")
include(":extensions:data-plane:data-plane-iam")
include(":core:common:token-core")
include(":core:common:lib:crypto-common-lib")
include(":core:common:lib:json-lib")
include(":core:common:lib:util-lib")
include(":core:common:lib:boot-lib")
include(":core:common:lib:http-lib")
include(":core:common:lib:keys-lib")
include(":core:common:lib:query-lib")
include(":core:common:lib:transform-lib")
include(":core:common:lib:state-machine-lib")
include(":core:common:lib:policy-engine-lib")
include(":core:common:lib:validator-lib")
include(":core:common:lib:json-ld-lib")
include(":core:common:lib:policy-evaluator-lib")
include(":core:common:junit")
include(":core:common:boot")
include(":core:common:connector-core")

include(":spi:common:core-spi")
include(":spi:common:jwt-signer-spi")
include(":spi:common:boot-spi")
include(":spi:common:keys-spi")
include(":spi:common:http-spi")
include(":spi:common:token-spi")
include(":spi:common:jwt-spi")
include(":spi:common:policy-model")
include(":spi:common:identity-did-spi")
include(":spi:common:identity-trust-spi")
include(":spi:common:verifiable-credentials-spi")
include(":spi:common:policy-engine-spi")
include(":spi:common:transaction-spi")
include(":spi:common:auth-spi")
include(":spi:common:transaction-datasource-spi")
include(":spi:common:transform-spi")
include(":spi:common:validator-spi")
include(":spi:common:web-spi")
include(":spi:common:json-ld-spi")
include(":spi:data-plane-selector:data-plane-selector-spi")
include(":spi:data-plane:data-plane-spi")
include(":tests:junit-base")
// GaussDB
include(":extensions:control-plane:store:asset-index-gaussdb")
include(":extensions:control-plane:store:contract-definition-store-gaussdb")
include(":extensions:control-plane:store:contract-negotiation-store-gaussdb")
include(":extensions:control-plane:store:transfer-process-store-gaussdb")
include(":extensions:control-plane:store:data-plane-instance-store-gaussdb")
include(":extensions:control-plane:store:policy-monitor-store-gaussdb")
include(":extensions:common:gaussdb:gaussdb-core")
include(":extensions:control-plane:store:policy-definition-store-gaussdb")
include(":e2e-tests")
