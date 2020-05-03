import {Component} from '@angular/core';
import {LoadingController} from '@ionic/angular';
import {RefresherEventDetail} from '@ionic/core';
import {tap} from 'rxjs/operators';
import {processLoading} from '../loading-controller.utils';
import {Proposal, ProposalsService} from './proposals.service';

@Component({
    selector: 'app-proposals',
    template: `
        <ion-header>
            <ion-toolbar>
                <ion-title i18n="@@proposal.title">Proposals</ion-title>
            </ion-toolbar>
        </ion-header>

        <ion-content *ngIf="proposals != null && proposals.length === 0">
            <ion-refresher slot="fixed" (ionRefresh)="onRefresh($event)" refreshingSpinner="circles" i18n-refreshingText="@@common.refreshing" refreshingText="Refreshing...">
                <ion-refresher-content></ion-refresher-content>
            </ion-refresher>
            <h1 i18n="@@proposal.noData">No proposal for this moment...</h1>
        </ion-content>

        <ion-content *ngIf="proposals != null && proposals.length !== 0">
            <ion-card>
                <ion-img [appProxifiedSrc]="currentProposal.profile.avatars[0]" style="height: 100%;"></ion-img>
                <ion-card-header>
                    <ion-card-title>{{currentProposal.profile.name}}</ion-card-title>
                    <ion-card-subtitle i18n="@@proposal.model.age">{currentProposal.profile.age, plural, =1 {1 year old} other {{{currentProposal.profile.age}} years old}}</ion-card-subtitle>
                </ion-card-header>
            </ion-card>

            <ion-fab vertical="bottom" slot="fixed" class="fab-space">
                <ion-fab-button (click)="clickOnUnlike()" color="danger">
                    <ion-icon name="close"></ion-icon>
                </ion-fab-button>
                <ion-fab-button (click)="clickOnLike()" color="success">
                    <ion-icon name="heart"></ion-icon>
                </ion-fab-button>
            </ion-fab>
        </ion-content>
    `,
    styles: [`
        /* Full screen without scroll */
        ion-card {
            margin: 0;
            height: 100%;
        }
    `, `
        .fab-space {
            width: 100%;
            display: flex;
            flex-direction: row;
            justify-content: space-evenly;
        }
    `],
})
export class ProposalsComponent {

    proposals: Proposal[];
    currentProposal: Proposal = undefined;

    constructor(loadingController: LoadingController, private service: ProposalsService) {
        processLoading(loadingController,
            this.service.getProposals()
                .pipe(tap(it => this.proposals = it))
                .pipe(tap(() => this.currentProposal = this.proposals[0])))
            .subscribe();
    }

    clickOnUnlike() {
        this.service.unlikeProposal(this.currentProposal.id)
            .pipe(tap(() => this.popNextProfile()))
            .subscribe();
    }

    clickOnLike() {
        this.service.likeProposal(this.currentProposal.id)
            .pipe(tap(() => this.popNextProfile()))
            .subscribe();
    }

    private popNextProfile() {
        this.proposals.splice(0, 1);
        this.currentProposal = this.proposals[0];
    }

    onRefresh(event: CustomEvent<RefresherEventDetail>) {
        this.service.getProposals()
            .pipe(tap(it => this.proposals = it))
            .pipe(tap(() => this.currentProposal = this.proposals[0]))
            .pipe(tap(event.detail.complete))
            .subscribe();
    }

}
