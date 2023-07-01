import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { DocumentDetailComponent } from './document-detail.component';

describe('Document Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DocumentDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: DocumentDetailComponent,
              resolve: { document: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(DocumentDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load document on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DocumentDetailComponent);

      // THEN
      expect(instance.document).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
