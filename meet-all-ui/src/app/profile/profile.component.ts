import {Component} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {LoadingController} from '@ionic/angular';
import {tap} from 'rxjs/operators';
import {Profile} from '../conversations/conversations.service';
import {processLoading} from '../loading-controller.utils';
import {ProfileService} from './profile.service';

@Component({
    selector: 'app-profile',
    template: `
        <ion-header>
            <ion-toolbar>
                <ion-buttons slot="start">
                    <ion-back-button></ion-back-button>
                </ion-buttons>
                <ion-title *ngIf="profile !== null">{{profile.name}}</ion-title>
            </ion-toolbar>
        </ion-header>

        <ion-content *ngIf="profile !== null">
            <ion-slides style="height: 80%;">
                <ion-slide *ngFor="let avatar of profile.avatars">
                    <a [href]="'https://meet-all-api.herokuapp.com/photo/' + avatar">
                        <ion-img [appProxifiedSrc]="avatar"></ion-img>
                    </a>
                </ion-slide>
            </ion-slides>
            <ion-card-header>
                <ion-card-title>{{profile.name}}</ion-card-title>
                <ion-card-subtitle i18n="@@profile.model.age">{profile.age, plural, =1 {1 year old} other {{{profile.age}} years old}}</ion-card-subtitle>
            </ion-card-header>
            <ion-card-content>
                {{profile.description}}
            </ion-card-content>
        </ion-content>`,
})
export class ProfileComponent {

    profile: Profile = null;

    constructor(
        activatedRoute: ActivatedRoute,
        loadingController: LoadingController,
        private service: ProfileService,
    ) {
        const profileId = activatedRoute.snapshot.paramMap.get('profileId');
        processLoading(loadingController,
            service.getProfile(profileId)
                .pipe(tap(it => this.profile = it))
        ).subscribe();
    }

}
