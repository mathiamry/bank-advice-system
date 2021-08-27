import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAdvisor } from '../advisor.model';

@Component({
  selector: 'jhi-advisor-detail',
  templateUrl: './advisor-detail.component.html',
})
export class AdvisorDetailComponent implements OnInit {
  advisor: IAdvisor | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ advisor }) => {
      this.advisor = advisor;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
