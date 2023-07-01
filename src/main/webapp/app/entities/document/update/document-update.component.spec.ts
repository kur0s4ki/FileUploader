import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DocumentFormService } from './document-form.service';
import { DocumentService } from '../service/document.service';
import { IDocument } from '../document.model';
import { IContent } from 'app/entities/content/content.model';
import { ContentService } from 'app/entities/content/service/content.service';
import { ICar } from 'app/entities/car/car.model';
import { CarService } from 'app/entities/car/service/car.service';

import { DocumentUpdateComponent } from './document-update.component';

describe('Document Management Update Component', () => {
  let comp: DocumentUpdateComponent;
  let fixture: ComponentFixture<DocumentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let documentFormService: DocumentFormService;
  let documentService: DocumentService;
  let contentService: ContentService;
  let carService: CarService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), DocumentUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(DocumentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DocumentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    documentFormService = TestBed.inject(DocumentFormService);
    documentService = TestBed.inject(DocumentService);
    contentService = TestBed.inject(ContentService);
    carService = TestBed.inject(CarService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call content query and add missing value', () => {
      const document: IDocument = { id: 456 };
      const content: IContent = { id: 63005 };
      document.content = content;

      const contentCollection: IContent[] = [{ id: 3011 }];
      jest.spyOn(contentService, 'query').mockReturnValue(of(new HttpResponse({ body: contentCollection })));
      const expectedCollection: IContent[] = [content, ...contentCollection];
      jest.spyOn(contentService, 'addContentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ document });
      comp.ngOnInit();

      expect(contentService.query).toHaveBeenCalled();
      expect(contentService.addContentToCollectionIfMissing).toHaveBeenCalledWith(contentCollection, content);
      expect(comp.contentsCollection).toEqual(expectedCollection);
    });

    it('Should call Car query and add missing value', () => {
      const document: IDocument = { id: 456 };
      const car: ICar = { id: 21169 };
      document.car = car;

      const carCollection: ICar[] = [{ id: 59748 }];
      jest.spyOn(carService, 'query').mockReturnValue(of(new HttpResponse({ body: carCollection })));
      const additionalCars = [car];
      const expectedCollection: ICar[] = [...additionalCars, ...carCollection];
      jest.spyOn(carService, 'addCarToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ document });
      comp.ngOnInit();

      expect(carService.query).toHaveBeenCalled();
      expect(carService.addCarToCollectionIfMissing).toHaveBeenCalledWith(carCollection, ...additionalCars.map(expect.objectContaining));
      expect(comp.carsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const document: IDocument = { id: 456 };
      const content: IContent = { id: 68851 };
      document.content = content;
      const car: ICar = { id: 37413 };
      document.car = car;

      activatedRoute.data = of({ document });
      comp.ngOnInit();

      expect(comp.contentsCollection).toContain(content);
      expect(comp.carsSharedCollection).toContain(car);
      expect(comp.document).toEqual(document);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDocument>>();
      const document = { id: 123 };
      jest.spyOn(documentFormService, 'getDocument').mockReturnValue(document);
      jest.spyOn(documentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ document });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: document }));
      saveSubject.complete();

      // THEN
      expect(documentFormService.getDocument).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(documentService.update).toHaveBeenCalledWith(expect.objectContaining(document));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDocument>>();
      const document = { id: 123 };
      jest.spyOn(documentFormService, 'getDocument').mockReturnValue({ id: null });
      jest.spyOn(documentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ document: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: document }));
      saveSubject.complete();

      // THEN
      expect(documentFormService.getDocument).toHaveBeenCalled();
      expect(documentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDocument>>();
      const document = { id: 123 };
      jest.spyOn(documentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ document });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(documentService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareContent', () => {
      it('Should forward to contentService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(contentService, 'compareContent');
        comp.compareContent(entity, entity2);
        expect(contentService.compareContent).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCar', () => {
      it('Should forward to carService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(carService, 'compareCar');
        comp.compareCar(entity, entity2);
        expect(carService.compareCar).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
