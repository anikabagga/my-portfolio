// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Collections;
import java.util.Comparator;

public final class FindMeetingQuery {

  static final int START_OF_DAY = TimeRange.START_OF_DAY;
  static final int END_OF_DAY = TimeRange.END_OF_DAY;

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> allAttendees = new ArrayList<>();
    allAttendees.addAll(request.getAttendees());
    allAttendees.addAll(request.getOptionalAttendees());
    MeetingRequest meetingWithEveryone = new MeetingRequest(allAttendees, request.getDuration());

    Collection<TimeRange> meetingTimesWithAll = findTime(events, meetingWithEveryone);

    // Returns meeting times based on if there are available times with optional attendees 
    if (meetingTimesWithAll.size() > 0 || request.getAttendees().isEmpty()) {
      return meetingTimesWithAll;
    } else {
      return findTime(events, request);
    }
  }

  public Collection<TimeRange> findTime(Collection<Event> events, MeetingRequest request) {
    
    List<TimeRange> possibleMeetingTimes = new ArrayList<>();
    List<TimeRange> blockedTimes = new ArrayList<>();
    int requestDuration = (int)request.getDuration();
    Collection<String> requestAttendees = request.getAttendees();
    int numEvents = events.size();
    int availableStart = START_OF_DAY;

    // No meeting times if duration is greater than whole day 
    if (requestDuration > TimeRange.WHOLE_DAY.duration()) {
      return possibleMeetingTimes;
    }

    // Check if no events happening
    if (numEvents == 0) {
      possibleMeetingTimes.add(TimeRange.WHOLE_DAY);
      return possibleMeetingTimes;
    }

    // Find events in which people in meeting request are in the event 
    for (Event event : events) {
      if (checkOverlap(event.getAttendees(), requestAttendees)) {
        blockedTimes.add(event.getWhen());
      }
    }

    // Order blocked meetings times based on start time 
    Collections.sort((List)blockedTimes, TimeRange.ORDER_BY_START);

    for (TimeRange e : blockedTimes) {
      int eventStart = e.start();
      int eventEnd = e.end();

      // Check for first event and overlapping events 
      if (eventStart == START_OF_DAY || eventStart < availableStart && eventEnd > availableStart) {
        availableStart = eventEnd;
      }
      
      if (availableStart <= eventStart) {
        if (hasEnoughMeetingTime(eventStart, availableStart, requestDuration)) {
          possibleMeetingTimes.add(TimeRange.fromStartEnd(availableStart, eventStart, false));
        }
        availableStart = eventEnd;
      }
    }

    // Add remaining time available after all events checked 
    if (hasEnoughTimeInDay(availableStart, requestDuration)) {
      possibleMeetingTimes.add(TimeRange.fromStartEnd(availableStart, END_OF_DAY, true));
    }
    return possibleMeetingTimes;
  }

  // Checks if any people in meeting request are in the event 
  public boolean checkOverlap(Collection<String> requestedAttendees, Collection<String> eventAttendees) {
    return !Collections.disjoint(requestedAttendees, eventAttendees);
  }

  public boolean hasEnoughMeetingTime(int eventStart, int availableStart, int requestDuration) {
    return eventStart - availableStart >= requestDuration;
  }

  public boolean hasEnoughTimeInDay(int availableStart, int requestDuration) {
    return availableStart + requestDuration <= END_OF_DAY;
  }
}
