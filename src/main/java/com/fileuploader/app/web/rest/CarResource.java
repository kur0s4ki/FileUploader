package com.fileuploader.app.web.rest;

import com.fileuploader.app.domain.Car;
import com.fileuploader.app.repository.CarRepository;
import com.fileuploader.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.fileuploader.app.domain.Car}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CarResource {

  private final Logger log = LoggerFactory.getLogger(CarResource.class);

  private static final String ENTITY_NAME = "car";

  @Value("${jhipster.clientApp.name}") private String applicationName;

  private final CarRepository carRepository;

  public CarResource(CarRepository carRepository) {
    this.carRepository = carRepository;
  }

  /**
   * {@code POST  /cars} : Create a new car.
   *
   * @param car the car to create.
   * @return the {@link ResponseEntity} with status {@code 201 (Created)} and
   *     with body the new car, or with status {@code 400 (Bad Request)} if the
   *     car has already an ID.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PostMapping("/cars")
  public ResponseEntity<Car> createCar(@Valid @RequestBody Car car)
      throws URISyntaxException {
    log.debug("REST request to save Car : {}", car);
    if (car.getId() != null) {
      throw new BadRequestAlertException("A new car cannot already have an ID",
                                         ENTITY_NAME, "idexists");
    }
    Car result = carRepository.save(car);
    return ResponseEntity.created(new URI("/api/cars/" + result.getId()))
        .headers(HeaderUtil.createEntityCreationAlert(
            applicationName, true, ENTITY_NAME, result.getId().toString()))
        .body(result);
  }

  /**
   * {@code PUT  /cars/:id} : Updates an existing car.
   *
   * @param id the id of the car to save.
   * @param car the car to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with
   *     body the updated car,
   * or with status {@code 400 (Bad Request)} if the car is not valid,
   * or with status {@code 500 (Internal Server Error)} if the car couldn't be
   * updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PutMapping("/cars/{id}")
  public ResponseEntity<Car>
  updateCar(@PathVariable(value = "id", required = false) final Long id,
            @Valid @RequestBody Car car) throws URISyntaxException {
    log.debug("REST request to update Car : {}, {}", id, car);
    if (car.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, car.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME,
                                         "idinvalid");
    }

    if (!carRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME,
                                         "idnotfound");
    }

    Car result = carRepository.save(car);
    return ResponseEntity.ok()
        .headers(HeaderUtil.createEntityUpdateAlert(
            applicationName, true, ENTITY_NAME, car.getId().toString()))
        .body(result);
  }

  /**
   * {@code PATCH  /cars/:id} : Partial updates given fields of an existing car,
   * field will ignore if it is null
   *
   * @param id the id of the car to save.
   * @param car the car to update.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with
   *     body the updated car,
   * or with status {@code 400 (Bad Request)} if the car is not valid,
   * or with status {@code 404 (Not Found)} if the car is not found,
   * or with status {@code 500 (Internal Server Error)} if the car couldn't be
   * updated.
   * @throws URISyntaxException if the Location URI syntax is incorrect.
   */
  @PatchMapping(value = "/cars/{id}",
                consumes = {"application/json", "application/merge-patch+json"})
  public ResponseEntity<Car>
  partialUpdateCar(@PathVariable(value = "id", required = false) final Long id,
                   @NotNull @RequestBody Car car) throws URISyntaxException {
    log.debug("REST request to partial update Car partially : {}, {}", id, car);
    if (car.getId() == null) {
      throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    }
    if (!Objects.equals(id, car.getId())) {
      throw new BadRequestAlertException("Invalid ID", ENTITY_NAME,
                                         "idinvalid");
    }

    if (!carRepository.existsById(id)) {
      throw new BadRequestAlertException("Entity not found", ENTITY_NAME,
                                         "idnotfound");
    }

    Optional<Car> result = carRepository.findById(car.getId())
                               .map(existingCar -> {
                                 if (car.getModel() != null) {
                                   existingCar.setModel(car.getModel());
                                 }

                                 return existingCar;
                               })
                               .map(carRepository::save);

    return ResponseUtil.wrapOrNotFound(
        result,
        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                                           car.getId().toString()));
  }

  /**
   * {@code GET  /cars} : get all the cars.
   *
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the
   *     list of cars in body.
   */
  @GetMapping("/cars")
  public List<Car> getAllCars() {
    log.debug("REST request to get all Cars");
    return carRepository.findAll();
  }

  /**
   * {@code GET  /cars/:id} : get the "id" car.
   *
   * @param id the id of the car to retrieve.
   * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with
   *     body the car, or with status {@code 404 (Not Found)}.
   */
  @GetMapping("/cars/{id}")
  public ResponseEntity<Car> getCar(@PathVariable Long id) {
    log.debug("REST request to get Car : {}", id);
    Optional<Car> car = carRepository.findById(id);
    return ResponseUtil.wrapOrNotFound(car);
  }

  /**
   * {@code DELETE  /cars/:id} : delete the "id" car.
   *
   * @param id the id of the car to delete.
   * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
   */
  @DeleteMapping("/cars/{id}")
  public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
    log.debug("REST request to delete Car : {}", id);
    carRepository.deleteById(id);
    return ResponseEntity.noContent()
        .headers(HeaderUtil.createEntityDeletionAlert(
            applicationName, true, ENTITY_NAME, id.toString()))
        .build();
  }
}
