package io.mosip.authentication.common.service.helper;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdType;

/**
 * @author Manoj SP
 *
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
public class AuditHelperTest {

	@Mock
	RestHelper restHelper;

	@InjectMocks
	AuditHelper auditHelper;

	@Mock
	IdInfoFetcherImpl idFetcherImpl;

	@Autowired
	MockMvc mockMvc;

	@Mock
	AuditRequestFactory auditFactory;

	@Mock
	RestRequestFactory restFactory;

	@Autowired
	Environment env;

	@Before
	public void before() {
		ReflectionTestUtils.setField(auditFactory, "env", env);
		ReflectionTestUtils.setField(restFactory, "env", env);
		ReflectionTestUtils.setField(auditHelper, "env", env);
	}

	@Test
	public void testAuditUtil() throws IDDataValidationException {
		auditHelper.audit(AuditModules.OTP_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, "id", IdType.UIN, "desc");
	}

	@Test
	public void TestcreateAuthTxn() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = createAuthRequestDto();
		RequestType requestType = RequestType.DEMO_AUTH;
		Mockito.when(idFetcherImpl.getUinOrVid(Mockito.any())).thenReturn(Optional.of("426789089018"));
		Mockito.when(idFetcherImpl.getUinOrVidType(Mockito.any())).thenReturn(IdType.UIN);
		auditHelper.createAuthTxn(authRequestDTO, "426789089018", requestType, "test134", false);
	}
	
	@Test
	public void TestCreateId() {
		ReflectionTestUtils.invokeMethod(auditHelper, "createId", "426789089018");
	}

	private AuthRequestDTO createAuthRequestDto() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setIndividualId("426789089018");
		return authRequestDTO;
	}

}
