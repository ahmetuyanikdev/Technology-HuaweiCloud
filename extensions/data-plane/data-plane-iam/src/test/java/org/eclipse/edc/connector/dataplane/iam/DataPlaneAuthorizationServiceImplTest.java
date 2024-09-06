/*
 *  Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

package org.eclipse.edc.connector.dataplane.iam;



import org.eclipse.edc.connector.dataplane.iam.service.DataPlaneAuthorizationServiceImpl;
import org.eclipse.edc.connector.dataplane.spi.AccessTokenData;
import org.eclipse.edc.connector.dataplane.spi.Endpoint;
import org.eclipse.edc.connector.dataplane.spi.iam.DataPlaneAccessControlService;
import org.eclipse.edc.connector.dataplane.spi.iam.DataPlaneAccessTokenService;
import org.eclipse.edc.connector.dataplane.spi.iam.PublicEndpointGeneratorService;
import org.eclipse.edc.spi.iam.ClaimToken;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowStartMessage;
import org.eclipse.edc.spi.types.domain.transfer.FlowType;
import org.eclipse.edc.spi.types.domain.transfer.TransferType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.Map;
import java.util.Set;

import static org.eclipse.edc.jwt.spi.JwtRegisteredClaimNames.AUDIENCE;
import static org.eclipse.edc.jwt.spi.JwtRegisteredClaimNames.ISSUED_AT;
import static org.eclipse.edc.jwt.spi.JwtRegisteredClaimNames.ISSUER;
import static org.eclipse.edc.jwt.spi.JwtRegisteredClaimNames.JWT_ID;
import static org.eclipse.edc.jwt.spi.JwtRegisteredClaimNames.SUBJECT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class DataPlaneAuthorizationServiceImplTest {

    public static final String OWN_PARTICIPANT_ID = "test-ownParticipantId";
    private final DataPlaneAccessTokenService accessTokenService = mock();
    private final PublicEndpointGeneratorService endpointGenerator = mock();
    private final DataPlaneAccessControlService accessControlService = mock();
    private final DataPlaneAuthorizationServiceImpl authorizationService = new DataPlaneAuthorizationServiceImpl(accessTokenService, endpointGenerator, accessControlService, OWN_PARTICIPANT_ID, Clock.systemUTC());

    @BeforeEach
    void setup() {
        when(endpointGenerator.generateFor(any(), any())).thenReturn(Result.success(Endpoint.url("http://example.com")));
    }

    @Test
    void createEndpointDataReference() {
        when(accessTokenService.obtainToken(any(), any(), anyMap())).thenReturn(Result.success(TokenRepresentation.Builder.newInstance().token("footoken").build()));
        var startMsg = createStartMessage()
                .transferType(new TransferType("DestinationType", FlowType.PULL))
                .build();

        var result = authorizationService.createEndpointDataReference(startMsg);

        var requiredClaims = Set.of(JWT_ID, AUDIENCE, ISSUER, SUBJECT, ISSUED_AT);

        verify(endpointGenerator).generateFor("DestinationType", startMsg.getSourceDataAddress());
    }

    @Test
    void createEndpointDataReference_withAuthType() {
        when(accessTokenService.obtainToken(any(), any(), anyMap())).thenReturn(Result.success(TokenRepresentation.Builder.newInstance()
                .token("footoken")
                .additional(Map.of("authType", "bearer", "fizz", "buzz"))
                .build()));
        var startMsg = createStartMessage().build();

        var result = authorizationService.createEndpointDataReference(startMsg);

    }


    @Test
    void authorize() {
        var claimToken = ClaimToken.Builder.newInstance().build();
        var address = DataAddress.Builder.newInstance().type("test-type").build();
        when(accessTokenService.resolve(eq("foo-token"))).thenReturn(Result.success(new AccessTokenData("test-id",
                claimToken,
                address)));
        when(accessControlService.checkAccess(eq(claimToken), eq(address), any(), anyMap())).thenReturn(Result.success());

        verifyNoMoreInteractions(accessTokenService, accessControlService);
    }

    private DataFlowStartMessage.Builder createStartMessage() {
        return DataFlowStartMessage.Builder.newInstance()
                .processId("test-processid")
                .transferType(new TransferType("DestinationType", FlowType.PULL))
                .agreementId("test-agreementid")
                .participantId("test-participantid")
                .assetId("test-assetid")
                .sourceDataAddress(DataAddress.Builder.newInstance().type("test-src").build())
                .destinationDataAddress(DataAddress.Builder.newInstance().type("test-dest").build())
                .properties(Map.of("foo", "bar", "fizz", "buzz"));
    }
}
