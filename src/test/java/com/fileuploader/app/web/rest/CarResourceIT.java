package com.fileuploader.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fileuploader.app.IntegrationTest;
import com.fileuploader.app.domain.Car;
import com.fileuploader.app.repository.CarRepository;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link CarResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CarResourceIT {

  private static final String DEFAULT_MODEL = "AAAAAAAAAA";
  private static final String UPDATED_MODEL = "BBBBBBBBBB";

  private static final String ENTITY_API_URL = "/api/cars";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

  private static Random random = new Random();
  private static AtomicLong count =
      new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

  @Autowired private CarRepository carRepository;

  @Autowired private EntityManager em;

  @Autowired private MockMvc restCarMockMvc;

  private Car car;

  /**
   * Create an entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Car createEntity(EntityManager em) {
    Car car = new Car().model(DEFAULT_MODEL);
    return car;
  }

  /**
   * Create an updated entity for this test.
   *
   * This is a static method, as tests for other entities might also need it,
   * if they test an entity which requires the current entity.
   */
  public static Car createUpdatedEntity(EntityManager em) {
    Car car = new Car().model(UPDATED_MODEL);
    return car;
  }

  @BeforeEach
  public void initTest() {
    car = createEntity(em);
  }

  @Test
  @Transactional
  void createCar() throws Exception {
    int databaseSizeBeforeCreate = carRepository.findAll().size();
    // Create the Car
    restCarMockMvc
        .perform(post(ENTITY_API_URL)
                     .contentType(MediaType.APPLICATION_JSON)
                     .content(TestUtil.convertObjectToJsonBytes(car)))
        .andExpect(status().isCreated());

    // Validate the Car in the database
    List<Car> carList = carRepository.findAll();
    assertThat(carList).hasSize(databaseSizeBeforeCreate + 1);
    Car testCar = carList.get(carList.size() - 1);
    assertThat(testCar.getModel()).isEqualTo(DEFAULT_MODEL);
  }

  @Test
  @Transactional
  void createCarWithExistingId() throws Exception {
    // Create the Car with an existing ID
    car.setId(1L);

    int databaseSizeBeforeCreate = carRepository.findAll().size();

    // An entity with an existing ID cannot be created, so this API call must
    // fail
    restCarMockMvc
        .perform(post(ENTITY_API_URL)
                     .contentType(MediaType.APPLICATION_JSON)
                     .content(TestUtil.convertObjectToJsonBytes(car)))
        .andExpect(status().isBadRequest());

    // Validate the Car in the database
    List<Car> carList = carRepository.findAll();
    assertThat(carList).hasSize(databaseSizeBeforeCreate);
  }

  @Test
  @Transactional
  void checkModelIsRequired() throws Exception {
    int databaseSizeBeforeTest = carRepository.findAll().size();
    // set the field null
    car.setModel(null);

    // Create the Car, which fails.

    restCarMockMvc
        .perform(post(ENTITY_API_URL)
                     .contentType(MediaType.APPLICATION_JSON)
                     .content(TestUtil.convertObjectToJsonBytes(car)))
        .andExpect(status().isBadRequest());

    List<Car> carList = carRepository.findAll();
    assertThat(carList).hasSize(databaseSizeBeforeTest);
  }

  @Test
  @Transactional
  void getAllCars() throws Exception {
    // Initialize the database
    carRepository.saveAndFlush(car);

    // Get all the carList
    restCarMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].id").value(hasItem(car.getId().intValue())))
        .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL)));
  }

  @Test
  @Transactional
  void getCar() throws Exception {
    // Initialize the database
    carRepository.saveAndFlush(car);

    // Get the car
    restCarMockMvc.perform(get(ENTITY_API_URL_ID, car.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id").value(car.getId().intValue()))
        .andExpect(jsonPath("$.model").value(DEFAULT_MODEL));
  }

  @Test
  @Transactional
  void getNonExistingCar() throws Exception {
    // Get the car
    restCarMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
        .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void putExistingCar() throws Exception {
    // Initialize the database
    carRepository.saveAndFlush(car);

    int databaseSizeBeforeUpdate = carRepository.findAll().size();

    // Update the car
    Car updatedCar = carRepository.findById(car.getId()).get();
    // Disconnect from session so that the updates on updatedCar are not
    // directly saved in db
    em.detach(updatedCar);
    updatedCar.model(UPDATED_MODEL);

    restCarMockMvc
        .perform(put(ENTITY_API_URL_ID, updatedCar.getId())
                     .contentType(MediaType.APPLICATION_JSON)
                     .content(TestUtil.convertObjectToJsonBytes(updatedCar)))
        .andExpect(status().isOk());

    // Validate the Car in the database
    List<Car> carList = carRepository.findAll();
    assertThat(carList).hasSize(databaseSizeBeforeUpdate);
    Car testCar = carList.get(carList.size() - 1);
    assertThat(testCar.getModel()).isEqualTo(UPDATED_MODEL);
  }

  @Test
  @Transactional
  void putNonExistingCar() throws Exception {
    int databaseSizeBeforeUpdate = carRepository.findAll().size();
    car.setId(count.incrementAndGet());

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restCarMockMvc
        .perform(put(ENTITY_API_URL_ID, car.getId())
                     .contentType(MediaType.APPLICATION_JSON)
                     .content(TestUtil.convertObjectToJsonBytes(car)))
        .andExpect(status().isBadRequest());

    // Validate the Car in the database
    List<Car> carList = carRepository.findAll();
    assertThat(carList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithIdMismatchCar() throws Exception {
    int databaseSizeBeforeUpdate = carRepository.findAll().size();
    car.setId(count.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restCarMockMvc
        .perform(put(ENTITY_API_URL_ID, count.incrementAndGet())
                     .contentType(MediaType.APPLICATION_JSON)
                     .content(TestUtil.convertObjectToJsonBytes(car)))
        .andExpect(status().isBadRequest());

    // Validate the Car in the database
    List<Car> carList = carRepository.findAll();
    assertThat(carList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void putWithMissingIdPathParamCar() throws Exception {
    int databaseSizeBeforeUpdate = carRepository.findAll().size();
    car.setId(count.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restCarMockMvc
        .perform(put(ENTITY_API_URL)
                     .contentType(MediaType.APPLICATION_JSON)
                     .content(TestUtil.convertObjectToJsonBytes(car)))
        .andExpect(status().isMethodNotAllowed());

    // Validate the Car in the database
    List<Car> carList = carRepository.findAll();
    assertThat(carList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void partialUpdateCarWithPatch() throws Exception {
    // Initialize the database
    carRepository.saveAndFlush(car);

    int databaseSizeBeforeUpdate = carRepository.findAll().size();

    // Update the car using partial update
    Car partialUpdatedCar = new Car();
    partialUpdatedCar.setId(car.getId());

    partialUpdatedCar.model(UPDATED_MODEL);

    restCarMockMvc
        .perform(
            patch(ENTITY_API_URL_ID, partialUpdatedCar.getId())
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCar)))
        .andExpect(status().isOk());

    // Validate the Car in the database
    List<Car> carList = carRepository.findAll();
    assertThat(carList).hasSize(databaseSizeBeforeUpdate);
    Car testCar = carList.get(carList.size() - 1);
    assertThat(testCar.getModel()).isEqualTo(UPDATED_MODEL);
  }

  @Test
  @Transactional
  void fullUpdateCarWithPatch() throws Exception {
    // Initialize the database
    carRepository.saveAndFlush(car);

    int databaseSizeBeforeUpdate = carRepository.findAll().size();

    // Update the car using partial update
    Car partialUpdatedCar = new Car();
    partialUpdatedCar.setId(car.getId());

    partialUpdatedCar.model(UPDATED_MODEL);

    restCarMockMvc
        .perform(
            patch(ENTITY_API_URL_ID, partialUpdatedCar.getId())
                .contentType("application/merge-patch+json")
                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCar)))
        .andExpect(status().isOk());

    // Validate the Car in the database
    List<Car> carList = carRepository.findAll();
    assertThat(carList).hasSize(databaseSizeBeforeUpdate);
    Car testCar = carList.get(carList.size() - 1);
    assertThat(testCar.getModel()).isEqualTo(UPDATED_MODEL);
  }

  @Test
  @Transactional
  void patchNonExistingCar() throws Exception {
    int databaseSizeBeforeUpdate = carRepository.findAll().size();
    car.setId(count.incrementAndGet());

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restCarMockMvc
        .perform(patch(ENTITY_API_URL_ID, car.getId())
                     .contentType("application/merge-patch+json")
                     .content(TestUtil.convertObjectToJsonBytes(car)))
        .andExpect(status().isBadRequest());

    // Validate the Car in the database
    List<Car> carList = carRepository.findAll();
    assertThat(carList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithIdMismatchCar() throws Exception {
    int databaseSizeBeforeUpdate = carRepository.findAll().size();
    car.setId(count.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restCarMockMvc
        .perform(patch(ENTITY_API_URL_ID, count.incrementAndGet())
                     .contentType("application/merge-patch+json")
                     .content(TestUtil.convertObjectToJsonBytes(car)))
        .andExpect(status().isBadRequest());

    // Validate the Car in the database
    List<Car> carList = carRepository.findAll();
    assertThat(carList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void patchWithMissingIdPathParamCar() throws Exception {
    int databaseSizeBeforeUpdate = carRepository.findAll().size();
    car.setId(count.incrementAndGet());

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restCarMockMvc
        .perform(patch(ENTITY_API_URL)
                     .contentType("application/merge-patch+json")
                     .content(TestUtil.convertObjectToJsonBytes(car)))
        .andExpect(status().isMethodNotAllowed());

    // Validate the Car in the database
    List<Car> carList = carRepository.findAll();
    assertThat(carList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  void deleteCar() throws Exception {
    // Initialize the database
    carRepository.saveAndFlush(car);

    int databaseSizeBeforeDelete = carRepository.findAll().size();

    // Delete the car
    restCarMockMvc
        .perform(delete(ENTITY_API_URL_ID, car.getId())
                     .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<Car> carList = carRepository.findAll();
    assertThat(carList).hasSize(databaseSizeBeforeDelete - 1);
  }
}
