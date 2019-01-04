import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {IonicModule} from '@ionic/angular';
import {SecurityModule} from '../../security';
import {ConversationsService} from '../conversations.service';
import {ConversationListPage} from './conversation-list.page';

@NgModule({
    imports: [
        // libs
        IonicModule,
        CommonModule,
        HttpClientModule,
        FormsModule,
        // app
        SecurityModule,
        RouterModule.forChild([{path: '', component: ConversationListPage}]),
    ],
    declarations: [
        ConversationListPage,
    ],
    providers: [
        ConversationsService,
    ],
})
export class ConversationListModule {
}
