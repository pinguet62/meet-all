import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';

@Injectable()
export class ConfigurationService {

    constructor(private http: HttpClient) {
    }

    setPosition(latitude: number, longitude: number, altitude: number): Observable<void> {
        const paramBuilder: HttpParams = new HttpParams()
            .append('latitude', latitude.toString())
            .append('longitude', longitude.toString())
            .append('altitude', altitude.toString());
        return this.http.post<void>(`${environment.apiUrl}/position?${paramBuilder.toString()}`, null);
    }

}
