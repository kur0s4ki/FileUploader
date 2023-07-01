import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'document',
        data: { pageTitle: 'fileUploaderApp.document.home.title' },
        loadChildren: () => import('./document/document.routes'),
      },
      {
        path: 'car',
        data: { pageTitle: 'fileUploaderApp.car.home.title' },
        loadChildren: () => import('./car/car.routes'),
      },
      {
        path: 'content',
        data: { pageTitle: 'fileUploaderApp.content.home.title' },
        loadChildren: () => import('./content/content.routes'),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
