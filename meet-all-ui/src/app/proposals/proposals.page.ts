import {Component} from '@angular/core';
import {LoadingController} from '@ionic/angular';
import {tap} from 'rxjs/operators';
import {Proposal, ProposalsService} from './proposals.service';
import {processLoading} from '../loading-controller.utils';

@Component({
    template: `
        <ion-header>
            <ion-toolbar>
                <ion-title>Proposals</ion-title>
            </ion-toolbar>
        </ion-header>

        <ion-content *ngIf="proposals != null && proposals.length === 0">
            <h1>No proposal for this moment...</h1>
        </ion-content>

        <ion-content *ngIf="proposals != null && proposals.length !== 0">
            <ion-card>
                <ion-img [src]="currentProposal.profile.avatars[0]" style="height: 100%;"></ion-img>
                <ion-card-header>
                    <ion-card-title>{{currentProposal.profile.name}}</ion-card-title>
                    <ion-card-subtitle>{{currentProposal.profile.age}} ans</ion-card-subtitle>
                </ion-card-header>
            </ion-card>
        </ion-content>
    `,
    styles: [`
        /* Full screen without scroll */
        ion-card {
            margin: 0;
            height: 100%;
        }
    `],
})
export class ProposalsPage {

    proposals: Proposal[];
    currentProposal: Proposal = undefined;

    constructor(loadingController: LoadingController, service: ProposalsService) {
        processLoading(loadingController,
            service.getProposals()
                .pipe(tap(it => this.proposals = it))
                .pipe(tap(() => this.currentProposal = this.proposals[0])))
            .subscribe();
    }

}
