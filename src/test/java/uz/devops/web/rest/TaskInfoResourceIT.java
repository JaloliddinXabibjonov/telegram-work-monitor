package uz.devops.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uz.devops.IntegrationTest;
import uz.devops.domain.TaskInfo;
import uz.devops.domain.enumeration.Status;
import uz.devops.repository.TaskInfoRepository;
import uz.devops.service.dto.TaskInfoDTO;
import uz.devops.service.mapper.TaskInfoMapper;

/**
 * Integration tests for the {@link TaskInfoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TaskInfoResourceIT {

    private static final String DEFAULT_PRICE = "12";
    private static final String UPDATED_PRICE = "2L";

    private static final Status DEFAULT_STATUS = Status.NEW;
    private static final Status UPDATED_STATUS = Status.ACTIVE;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/task-infos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TaskInfoRepository taskInfoRepository;

    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaskInfoMockMvc;

    private TaskInfo taskInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskInfo createEntity(EntityManager em) {
        TaskInfo taskInfo = new TaskInfo().price(DEFAULT_PRICE).status(DEFAULT_STATUS).description(DEFAULT_DESCRIPTION);
        return taskInfo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskInfo createUpdatedEntity(EntityManager em) {
        TaskInfo taskInfo = new TaskInfo().price(UPDATED_PRICE).status(UPDATED_STATUS).description(UPDATED_DESCRIPTION);
        return taskInfo;
    }

    @BeforeEach
    public void initTest() {
        taskInfo = createEntity(em);
    }

    @Test
    @Transactional
    void createTaskInfo() throws Exception {
        int databaseSizeBeforeCreate = taskInfoRepository.findAll().size();
        // Create the TaskInfo
        TaskInfoDTO taskInfoDTO = taskInfoMapper.toDto(taskInfo);
        restTaskInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskInfoDTO)))
            .andExpect(status().isCreated());

        // Validate the TaskInfo in the database
        List<TaskInfo> taskInfoList = taskInfoRepository.findAll();
        assertThat(taskInfoList).hasSize(databaseSizeBeforeCreate + 1);
        TaskInfo testTaskInfo = taskInfoList.get(taskInfoList.size() - 1);
        assertThat(testTaskInfo.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testTaskInfo.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testTaskInfo.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createTaskInfoWithExistingId() throws Exception {
        // Create the TaskInfo with an existing ID
        taskInfo.setId(1L);
        TaskInfoDTO taskInfoDTO = taskInfoMapper.toDto(taskInfo);

        int databaseSizeBeforeCreate = taskInfoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskInfoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskInfoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TaskInfo in the database
        List<TaskInfo> taskInfoList = taskInfoRepository.findAll();
        assertThat(taskInfoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTaskInfos() throws Exception {
        // Initialize the database
        taskInfoRepository.saveAndFlush(taskInfo);

        // Get all the taskInfoList
        restTaskInfoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    void getTaskInfo() throws Exception {
        // Initialize the database
        taskInfoRepository.saveAndFlush(taskInfo);

        // Get the taskInfo
        restTaskInfoMockMvc
            .perform(get(ENTITY_API_URL_ID, taskInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(taskInfo.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTaskInfo() throws Exception {
        // Get the taskInfo
        restTaskInfoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTaskInfo() throws Exception {
        // Initialize the database
        taskInfoRepository.saveAndFlush(taskInfo);

        int databaseSizeBeforeUpdate = taskInfoRepository.findAll().size();

        // Update the taskInfo
        TaskInfo updatedTaskInfo = taskInfoRepository.findById(taskInfo.getId()).get();
        // Disconnect from session so that the updates on updatedTaskInfo are not directly saved in db
        em.detach(updatedTaskInfo);
        updatedTaskInfo.price(UPDATED_PRICE).status(UPDATED_STATUS).description(UPDATED_DESCRIPTION);
        TaskInfoDTO taskInfoDTO = taskInfoMapper.toDto(updatedTaskInfo);

        restTaskInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskInfoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskInfoDTO))
            )
            .andExpect(status().isOk());

        // Validate the TaskInfo in the database
        List<TaskInfo> taskInfoList = taskInfoRepository.findAll();
        assertThat(taskInfoList).hasSize(databaseSizeBeforeUpdate);
        TaskInfo testTaskInfo = taskInfoList.get(taskInfoList.size() - 1);
        assertThat(testTaskInfo.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testTaskInfo.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTaskInfo.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingTaskInfo() throws Exception {
        int databaseSizeBeforeUpdate = taskInfoRepository.findAll().size();
        taskInfo.setId(count.incrementAndGet());

        // Create the TaskInfo
        TaskInfoDTO taskInfoDTO = taskInfoMapper.toDto(taskInfo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskInfoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskInfo in the database
        List<TaskInfo> taskInfoList = taskInfoRepository.findAll();
        assertThat(taskInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTaskInfo() throws Exception {
        int databaseSizeBeforeUpdate = taskInfoRepository.findAll().size();
        taskInfo.setId(count.incrementAndGet());

        // Create the TaskInfo
        TaskInfoDTO taskInfoDTO = taskInfoMapper.toDto(taskInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskInfoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskInfo in the database
        List<TaskInfo> taskInfoList = taskInfoRepository.findAll();
        assertThat(taskInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTaskInfo() throws Exception {
        int databaseSizeBeforeUpdate = taskInfoRepository.findAll().size();
        taskInfo.setId(count.incrementAndGet());

        // Create the TaskInfo
        TaskInfoDTO taskInfoDTO = taskInfoMapper.toDto(taskInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskInfoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskInfoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskInfo in the database
        List<TaskInfo> taskInfoList = taskInfoRepository.findAll();
        assertThat(taskInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTaskInfoWithPatch() throws Exception {
        // Initialize the database
        taskInfoRepository.saveAndFlush(taskInfo);

        int databaseSizeBeforeUpdate = taskInfoRepository.findAll().size();

        // Update the taskInfo using partial update
        TaskInfo partialUpdatedTaskInfo = new TaskInfo();
        partialUpdatedTaskInfo.setId(taskInfo.getId());

        partialUpdatedTaskInfo.price(UPDATED_PRICE).status(UPDATED_STATUS).description(UPDATED_DESCRIPTION);

        restTaskInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTaskInfo))
            )
            .andExpect(status().isOk());

        // Validate the TaskInfo in the database
        List<TaskInfo> taskInfoList = taskInfoRepository.findAll();
        assertThat(taskInfoList).hasSize(databaseSizeBeforeUpdate);
        TaskInfo testTaskInfo = taskInfoList.get(taskInfoList.size() - 1);
        assertThat(testTaskInfo.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testTaskInfo.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTaskInfo.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateTaskInfoWithPatch() throws Exception {
        // Initialize the database
        taskInfoRepository.saveAndFlush(taskInfo);

        int databaseSizeBeforeUpdate = taskInfoRepository.findAll().size();

        // Update the taskInfo using partial update
        TaskInfo partialUpdatedTaskInfo = new TaskInfo();
        partialUpdatedTaskInfo.setId(taskInfo.getId());

        partialUpdatedTaskInfo.price(UPDATED_PRICE).status(UPDATED_STATUS).description(UPDATED_DESCRIPTION);

        restTaskInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskInfo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTaskInfo))
            )
            .andExpect(status().isOk());

        // Validate the TaskInfo in the database
        List<TaskInfo> taskInfoList = taskInfoRepository.findAll();
        assertThat(taskInfoList).hasSize(databaseSizeBeforeUpdate);
        TaskInfo testTaskInfo = taskInfoList.get(taskInfoList.size() - 1);
        assertThat(testTaskInfo.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testTaskInfo.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTaskInfo.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingTaskInfo() throws Exception {
        int databaseSizeBeforeUpdate = taskInfoRepository.findAll().size();
        taskInfo.setId(count.incrementAndGet());

        // Create the TaskInfo
        TaskInfoDTO taskInfoDTO = taskInfoMapper.toDto(taskInfo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, taskInfoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taskInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskInfo in the database
        List<TaskInfo> taskInfoList = taskInfoRepository.findAll();
        assertThat(taskInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTaskInfo() throws Exception {
        int databaseSizeBeforeUpdate = taskInfoRepository.findAll().size();
        taskInfo.setId(count.incrementAndGet());

        // Create the TaskInfo
        TaskInfoDTO taskInfoDTO = taskInfoMapper.toDto(taskInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskInfoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taskInfoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskInfo in the database
        List<TaskInfo> taskInfoList = taskInfoRepository.findAll();
        assertThat(taskInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTaskInfo() throws Exception {
        int databaseSizeBeforeUpdate = taskInfoRepository.findAll().size();
        taskInfo.setId(count.incrementAndGet());

        // Create the TaskInfo
        TaskInfoDTO taskInfoDTO = taskInfoMapper.toDto(taskInfo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskInfoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(taskInfoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskInfo in the database
        List<TaskInfo> taskInfoList = taskInfoRepository.findAll();
        assertThat(taskInfoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTaskInfo() throws Exception {
        // Initialize the database
        taskInfoRepository.saveAndFlush(taskInfo);

        int databaseSizeBeforeDelete = taskInfoRepository.findAll().size();

        // Delete the taskInfo
        restTaskInfoMockMvc
            .perform(delete(ENTITY_API_URL_ID, taskInfo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TaskInfo> taskInfoList = taskInfoRepository.findAll();
        assertThat(taskInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
