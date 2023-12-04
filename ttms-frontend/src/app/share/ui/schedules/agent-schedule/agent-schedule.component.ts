import { Component, ComponentRef, Input, OnDestroy, OnInit, ViewContainerRef } from '@angular/core';
import { CalendarOptions, EventSourceInput } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import { Observable, Subscription } from 'rxjs';
import { ICalendarEvent } from 'src/app/share/data-access/types/calendar/calender-event.interface';
import { IClientSchedule, IEventSchedule, ITripSchedule } from 'src/app/share/data-access/types/calendar/client-schedule.interface';
import { EventTooltipComponent } from '../event-tooltip/event-tooltip.component';
import { Router } from '@angular/router';

@Component({
  selector: 'agent-schedule',
  templateUrl: './agent-schedule.component.html',
  styleUrls: ['./agent-schedule.component.scss']
})
export class AgentScheduleComponent implements OnInit, OnDestroy{
  @Input() profileSchedule$!: Observable<IClientSchedule[]>
  profileScheduleSub!: Subscription
  events: ICalendarEvent[] = []

  calendarOptions: CalendarOptions = {
    initialView: 'dayGridMonth',
    events: [this.events],
    plugins: [dayGridPlugin],
    eventClick: this.handleEventClick.bind(this),
    eventContent: this.getEventContent.bind(this),
    eventMinHeight: 5
  };

  private handleEventClick(info: any): void {
    const eventDetail = <ICalendarEvent>info.event.extendedProps;
    this.route.navigate([`trip/details/${eventDetail.tripId}`])
  }

  private getEventContent(info: any): any {
    const clientId = info.event.extendedProps.clientId
    const tripId = info.event.extendedProps.tripId
  
    const eventElement = document.createElement('div');
    eventElement.innerHTML = `${info.event.title}`;
  
    if (clientId && tripId) {
      eventElement.style.backgroundColor = this.getColorByIds(clientId, tripId);
      eventElement.className = "calendar-event"
    }
  
    return { domNodes: [eventElement] };
  }
  

  private getColorByIds(clientId: number, tripId: number): string {
    const hue = ((clientId * 137) + tripId) % 360; 
    const saturation = 75;
    const lightness = 50;

    return `hsl(${hue}, ${saturation}%, ${lightness}%)`;
  }

  constructor(private route: Router){}

  ngOnInit(): void {
    this.profileScheduleSub =  this.profileSchedule$.subscribe((clientSchedules) => {
      this.events = clientSchedules
        .flatMap((clientSchedule) =>
          clientSchedule.trips
            .filter((tripSchedule) => tripSchedule.events.length > 0)
            .flatMap((tripSchedule) =>
              tripSchedule.events.map((event) => ({
                id: event.eventId,
                title: event.eventName,
                date: event.eventDate,
                tripName: tripSchedule.tripName,
                description: event.eventDescription ?? '',
                clientId: tripSchedule.clientId ?? 0,
                tripId: tripSchedule.tripId,
                clientFirst: clientSchedule.firstname,
                clientLast: clientSchedule.lastname
              }))
            )
        );
  
      this.calendarOptions.events = this.events as EventSourceInput;
    });
  }

  ngOnDestroy(): void {
    this.profileScheduleSub.unsubscribe
  }
}
