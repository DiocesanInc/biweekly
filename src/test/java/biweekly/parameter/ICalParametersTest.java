package biweekly.parameter;

import static biweekly.util.TestUtils.assertWarnings;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import biweekly.ICalDataType;
import biweekly.ICalVersion;

/*
 Copyright (c) 2013, Michael Angstadt
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met: 

 1. Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer. 
 2. Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution. 

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * @author Michael Angstadt
 */
public class ICalParametersTest {
	private ICalParameters params;

	@Before
	public void before() {
		params = new ICalParameters();
	}

	@Test
	public void case_insensitive() {
		//tests to make sure sanitizeKey() is implemented correctly
		//ListMultimapTest tests the rest of the get/put/remove methods
		params.put("NUMBERS", "1");
		assertEquals("1", params.first("numbers"));
	}

	@Test
	public void rsvp() {
		assertNull(params.getRsvp());
		params.setRsvp(true);
		assertTrue(params.getRsvp());
		params.setRsvp(false);
		assertFalse(params.getRsvp());
	}

	@Test(expected = IllegalStateException.class)
	public void rsvp_malformed() {
		params.put("RSVP", "invalid");
		params.getRsvp();
	}

	@Test
	public void validate_empty() {
		assertWarnings(0, params.validate(ICalVersion.V2_0));
	}

	@Test
	public void validate_rsvp() {
		params.replace(ICalParameters.RSVP, "foo");
		assertWarnings(1, params.validate(ICalVersion.V2_0));

		params.replace(ICalParameters.RSVP, "true");
		assertWarnings(0, params.validate(ICalVersion.V2_0));

		params.replace(ICalParameters.RSVP, "false");
		assertWarnings(0, params.validate(ICalVersion.V2_0));

		params.replace(ICalParameters.RSVP, "TRUE");
		assertWarnings(0, params.validate(ICalVersion.V2_0));

		params.replace(ICalParameters.RSVP, "FALSE");
		assertWarnings(0, params.validate(ICalVersion.V2_0));
	}

	@Test
	public void validate_bad_values() {
		params.put(ICalParameters.CUTYPE, "foo");
		params.put(ICalParameters.FBTYPE, "foo");
		params.put(ICalParameters.PARTSTAT, "foo");
		params.put(ICalParameters.RANGE, "foo");
		params.put(ICalParameters.RELATED, "foo");
		params.put(ICalParameters.RELTYPE, "foo");
		params.put(ICalParameters.ROLE, "foo");
		params.put(ICalParameters.VALUE, "foo");

		assertWarnings(8, params.validate(ICalVersion.V2_0));
	}

	@Test
	public void validate_good_values() {
		params.put(ICalParameters.CUTYPE, CalendarUserType.GROUP.getValue());
		params.put(ICalParameters.FBTYPE, FreeBusyType.BUSY.getValue());
		params.put(ICalParameters.PARTSTAT, ParticipationStatus.ACCEPTED.getValue());
		params.put(ICalParameters.RANGE, Range.THIS_AND_FUTURE.getValue());
		params.put(ICalParameters.RELATED, Related.END.getValue());
		params.put(ICalParameters.RELTYPE, RelationshipType.CHILD.getValue());
		params.put(ICalParameters.ROLE, Role.CHAIR.getValue());
		params.put(ICalParameters.VALUE, ICalDataType.BINARY.getName());

		assertWarnings(0, params.validate(ICalVersion.V2_0));
	}
}
