package br.com.reactit.docmanagement.domain;

import static br.com.reactit.docmanagement.domain.DocumentTestSamples.*;
import static br.com.reactit.docmanagement.domain.FolderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import br.com.reactit.docmanagement.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DocumentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Document.class);
        Document document1 = getDocumentSample1();
        Document document2 = new Document();
        assertThat(document1).isNotEqualTo(document2);

        document2.setId(document1.getId());
        assertThat(document1).isEqualTo(document2);

        document2 = getDocumentSample2();
        assertThat(document1).isNotEqualTo(document2);
    }

    @Test
    void folderTest() throws Exception {
        Document document = getDocumentRandomSampleGenerator();
        Folder folderBack = getFolderRandomSampleGenerator();

        document.setFolder(folderBack);
        assertThat(document.getFolder()).isEqualTo(folderBack);

        document.folder(null);
        assertThat(document.getFolder()).isNull();
    }
}
