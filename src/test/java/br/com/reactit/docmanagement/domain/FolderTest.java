package br.com.reactit.docmanagement.domain;

import static br.com.reactit.docmanagement.domain.FolderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import br.com.reactit.docmanagement.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FolderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Folder.class);
        Folder folder1 = getFolderSample1();
        Folder folder2 = new Folder();
        assertThat(folder1).isNotEqualTo(folder2);

        folder2.setId(folder1.getId());
        assertThat(folder1).isEqualTo(folder2);

        folder2 = getFolderSample2();
        assertThat(folder1).isNotEqualTo(folder2);
    }
}
