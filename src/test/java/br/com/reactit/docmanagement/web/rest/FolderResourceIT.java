package br.com.reactit.docmanagement.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.reactit.docmanagement.IntegrationTest;
import br.com.reactit.docmanagement.domain.Folder;
import br.com.reactit.docmanagement.repository.FolderRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FolderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FolderResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/folders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFolderMockMvc;

    private Folder folder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Folder createEntity(EntityManager em) {
        Folder folder = new Folder().title(DEFAULT_TITLE).description(DEFAULT_DESCRIPTION).created(DEFAULT_CREATED);
        return folder;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Folder createUpdatedEntity(EntityManager em) {
        Folder folder = new Folder().title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).created(UPDATED_CREATED);
        return folder;
    }

    @BeforeEach
    public void initTest() {
        folder = createEntity(em);
    }

    @Test
    @Transactional
    void createFolder() throws Exception {
        int databaseSizeBeforeCreate = folderRepository.findAll().size();
        // Create the Folder
        restFolderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(folder)))
            .andExpect(status().isCreated());

        // Validate the Folder in the database
        List<Folder> folderList = folderRepository.findAll();
        assertThat(folderList).hasSize(databaseSizeBeforeCreate + 1);
        Folder testFolder = folderList.get(folderList.size() - 1);
        assertThat(testFolder.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testFolder.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFolder.getCreated()).isEqualTo(DEFAULT_CREATED);
    }

    @Test
    @Transactional
    void createFolderWithExistingId() throws Exception {
        // Create the Folder with an existing ID
        folder.setId(1L);

        int databaseSizeBeforeCreate = folderRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFolderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(folder)))
            .andExpect(status().isBadRequest());

        // Validate the Folder in the database
        List<Folder> folderList = folderRepository.findAll();
        assertThat(folderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = folderRepository.findAll().size();
        // set the field null
        folder.setTitle(null);

        // Create the Folder, which fails.

        restFolderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(folder)))
            .andExpect(status().isBadRequest());

        List<Folder> folderList = folderRepository.findAll();
        assertThat(folderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFolders() throws Exception {
        // Initialize the database
        folderRepository.saveAndFlush(folder);

        // Get all the folderList
        restFolderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(folder.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())));
    }

    @Test
    @Transactional
    void getFolder() throws Exception {
        // Initialize the database
        folderRepository.saveAndFlush(folder);

        // Get the folder
        restFolderMockMvc
            .perform(get(ENTITY_API_URL_ID, folder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(folder.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFolder() throws Exception {
        // Get the folder
        restFolderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFolder() throws Exception {
        // Initialize the database
        folderRepository.saveAndFlush(folder);

        int databaseSizeBeforeUpdate = folderRepository.findAll().size();

        // Update the folder
        Folder updatedFolder = folderRepository.findById(folder.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFolder are not directly saved in db
        em.detach(updatedFolder);
        updatedFolder.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).created(UPDATED_CREATED);

        restFolderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFolder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFolder))
            )
            .andExpect(status().isOk());

        // Validate the Folder in the database
        List<Folder> folderList = folderRepository.findAll();
        assertThat(folderList).hasSize(databaseSizeBeforeUpdate);
        Folder testFolder = folderList.get(folderList.size() - 1);
        assertThat(testFolder.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testFolder.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFolder.getCreated()).isEqualTo(UPDATED_CREATED);
    }

    @Test
    @Transactional
    void putNonExistingFolder() throws Exception {
        int databaseSizeBeforeUpdate = folderRepository.findAll().size();
        folder.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFolderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, folder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(folder))
            )
            .andExpect(status().isBadRequest());

        // Validate the Folder in the database
        List<Folder> folderList = folderRepository.findAll();
        assertThat(folderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFolder() throws Exception {
        int databaseSizeBeforeUpdate = folderRepository.findAll().size();
        folder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFolderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(folder))
            )
            .andExpect(status().isBadRequest());

        // Validate the Folder in the database
        List<Folder> folderList = folderRepository.findAll();
        assertThat(folderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFolder() throws Exception {
        int databaseSizeBeforeUpdate = folderRepository.findAll().size();
        folder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFolderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(folder)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Folder in the database
        List<Folder> folderList = folderRepository.findAll();
        assertThat(folderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFolderWithPatch() throws Exception {
        // Initialize the database
        folderRepository.saveAndFlush(folder);

        int databaseSizeBeforeUpdate = folderRepository.findAll().size();

        // Update the folder using partial update
        Folder partialUpdatedFolder = new Folder();
        partialUpdatedFolder.setId(folder.getId());

        partialUpdatedFolder.created(UPDATED_CREATED);

        restFolderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFolder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFolder))
            )
            .andExpect(status().isOk());

        // Validate the Folder in the database
        List<Folder> folderList = folderRepository.findAll();
        assertThat(folderList).hasSize(databaseSizeBeforeUpdate);
        Folder testFolder = folderList.get(folderList.size() - 1);
        assertThat(testFolder.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testFolder.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testFolder.getCreated()).isEqualTo(UPDATED_CREATED);
    }

    @Test
    @Transactional
    void fullUpdateFolderWithPatch() throws Exception {
        // Initialize the database
        folderRepository.saveAndFlush(folder);

        int databaseSizeBeforeUpdate = folderRepository.findAll().size();

        // Update the folder using partial update
        Folder partialUpdatedFolder = new Folder();
        partialUpdatedFolder.setId(folder.getId());

        partialUpdatedFolder.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).created(UPDATED_CREATED);

        restFolderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFolder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFolder))
            )
            .andExpect(status().isOk());

        // Validate the Folder in the database
        List<Folder> folderList = folderRepository.findAll();
        assertThat(folderList).hasSize(databaseSizeBeforeUpdate);
        Folder testFolder = folderList.get(folderList.size() - 1);
        assertThat(testFolder.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testFolder.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testFolder.getCreated()).isEqualTo(UPDATED_CREATED);
    }

    @Test
    @Transactional
    void patchNonExistingFolder() throws Exception {
        int databaseSizeBeforeUpdate = folderRepository.findAll().size();
        folder.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFolderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, folder.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(folder))
            )
            .andExpect(status().isBadRequest());

        // Validate the Folder in the database
        List<Folder> folderList = folderRepository.findAll();
        assertThat(folderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFolder() throws Exception {
        int databaseSizeBeforeUpdate = folderRepository.findAll().size();
        folder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFolderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(folder))
            )
            .andExpect(status().isBadRequest());

        // Validate the Folder in the database
        List<Folder> folderList = folderRepository.findAll();
        assertThat(folderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFolder() throws Exception {
        int databaseSizeBeforeUpdate = folderRepository.findAll().size();
        folder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFolderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(folder)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Folder in the database
        List<Folder> folderList = folderRepository.findAll();
        assertThat(folderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFolder() throws Exception {
        // Initialize the database
        folderRepository.saveAndFlush(folder);

        int databaseSizeBeforeDelete = folderRepository.findAll().size();

        // Delete the folder
        restFolderMockMvc
            .perform(delete(ENTITY_API_URL_ID, folder.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Folder> folderList = folderRepository.findAll();
        assertThat(folderList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
