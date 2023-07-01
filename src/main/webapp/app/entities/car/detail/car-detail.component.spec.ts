import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CarDetailComponent } from './car-detail.component';

describe('Car Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CarDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: CarDetailComponent,
              resolve: { car: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(CarDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load car on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CarDetailComponent);

      // THEN
      expect(instance.car).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
