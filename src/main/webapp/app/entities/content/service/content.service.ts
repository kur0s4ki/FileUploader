import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IContent, NewContent } from '../content.model';

export type PartialUpdateContent = Partial<IContent> & Pick<IContent, 'id'>;

export type EntityResponseType = HttpResponse<IContent>;
export type EntityArrayResponseType = HttpResponse<IContent[]>;

@Injectable({ providedIn: 'root' })
export class ContentService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/contents');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(content: NewContent): Observable<EntityResponseType> {
    return this.http.post<IContent>(this.resourceUrl, content, { observe: 'response' });
  }

  update(content: IContent): Observable<EntityResponseType> {
    return this.http.put<IContent>(`${this.resourceUrl}/${this.getContentIdentifier(content)}`, content, { observe: 'response' });
  }

  partialUpdate(content: PartialUpdateContent): Observable<EntityResponseType> {
    return this.http.patch<IContent>(`${this.resourceUrl}/${this.getContentIdentifier(content)}`, content, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IContent>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IContent[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getContentIdentifier(content: Pick<IContent, 'id'>): number {
    return content.id;
  }

  compareContent(o1: Pick<IContent, 'id'> | null, o2: Pick<IContent, 'id'> | null): boolean {
    return o1 && o2 ? this.getContentIdentifier(o1) === this.getContentIdentifier(o2) : o1 === o2;
  }

  addContentToCollectionIfMissing<Type extends Pick<IContent, 'id'>>(
    contentCollection: Type[],
    ...contentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const contents: Type[] = contentsToCheck.filter(isPresent);
    if (contents.length > 0) {
      const contentCollectionIdentifiers = contentCollection.map(contentItem => this.getContentIdentifier(contentItem)!);
      const contentsToAdd = contents.filter(contentItem => {
        const contentIdentifier = this.getContentIdentifier(contentItem);
        if (contentCollectionIdentifiers.includes(contentIdentifier)) {
          return false;
        }
        contentCollectionIdentifiers.push(contentIdentifier);
        return true;
      });
      return [...contentsToAdd, ...contentCollection];
    }
    return contentCollection;
  }
}
