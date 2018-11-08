import {RouterModule} from '@angular/router';
import {NgModule} from '@angular/core';
import {ConversationsPage} from './conversations.page';

@NgModule({
    imports: [
        RouterModule.forChild([{path: '', component: ConversationsPage}]),
    ],
    exports: [RouterModule],
})
export class ConversationsRoutingModule {
}
