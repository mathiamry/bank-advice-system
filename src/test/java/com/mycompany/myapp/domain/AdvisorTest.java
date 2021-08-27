package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AdvisorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Advisor.class);
        Advisor advisor1 = new Advisor();
        advisor1.setId(1L);
        Advisor advisor2 = new Advisor();
        advisor2.setId(advisor1.getId());
        assertThat(advisor1).isEqualTo(advisor2);
        advisor2.setId(2L);
        assertThat(advisor1).isNotEqualTo(advisor2);
        advisor1.setId(null);
        assertThat(advisor1).isNotEqualTo(advisor2);
    }
}
