import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './share/feature/home/home.component';
import { authGuardFn } from './share/data-access/services/auth/auth.guard';
import { LoginComponent } from './share/feature/login/login.component';
import { ViewAgentComponent } from './agent/feature/view-agent/view-agent.component';
import { AddAgentComponent } from './agent/feature/add-agent/add-agent.component';
import { AgentDetailsComponent } from './agent/feature/agent-details/agent-details.component';
import { ProfileDetailsComponent } from './profile/feature/profile-details/profile-details.component';
import { CreateClientComponent } from './client/feature/create-client/create-client.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', component: HomeComponent, canActivate: [authGuardFn], data: { role: ['ADMIN', 'AGENT'] }},
  { path: 'agent', component: ViewAgentComponent, canActivate: [authGuardFn], data: { role: ['ADMIN', 'AGENT'] }},
  { path: 'agent/add', component: AddAgentComponent, canActivate: [authGuardFn], data: { role: ['ADMIN'] }},
  { path: 'agent/:id', component: AgentDetailsComponent, canActivate: [authGuardFn], data: { role: ['ADMIN', 'AGENT'] }},
  { path: 'client/create', component: CreateClientComponent, canActivate: [authGuardFn], data: { role: ['ADMIN', 'AGENT'] }},
  { path: 'profile', component: ProfileDetailsComponent, canActivate: [authGuardFn], data: { role: ['ADMIN', 'AGENT'] }} 
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
