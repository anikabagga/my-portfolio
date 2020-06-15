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
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> mandatory_attendees = request.getAttendees();
    Collection<String> optional_attendees = request.getOptionalAttendees();
    Collection<String> allAttendees = new ArrayList<>();
    allAttendees.addAll(mandatory_attendees);
    allAttendees.addAll(optional_attendees);
    MeetingRequest meeting_with_all_attendees = new MeetingRequest(allAttendees, request.getDuration());

    Collection<TimeRange> meeting_with_all = findTime(events, meeting_with_all_attendees);

    // Returns meeting times based on if there are available times with optional attendees 
    if (meeting_with_all.size() > 0 || mandatory_attendees.isEmpty()) {
      return meeting_with_all;
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
    int availableStart = TimeRange.START_OF_DAY;
    int event_start;
    int event_end;
    int counter = 0;
    Set<String> eventAttendees;

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
      counter++;
      event_start = e.start();
      event_end = e.end();

      if (event_start == TimeRange.START_OF_DAY) {
        availableStart = event_end;
      }

      // Check for overlapping events
      if (event_start < availableStart) {
        if (event_end > availableStart) {
        availableStart = event_end;
        }
      }

      if (availableStart <= event_start) {
        if (event_start - availableStart >= requestDuration) {
        possibleMeetingTimes.add(TimeRange.fromStartEnd(availableStart, event_start, false));
        }
        availableStart = event_end;
      }

      // If last event, add remaining time available in day 
      if (counter == numEvents) {
        if (availableStart + requestDuration <= TimeRange.END_OF_DAY) {
        possibleMeetingTimes.add(TimeRange.fromStartEnd(availableStart, TimeRange.END_OF_DAY, true));
        availableStart = TimeRange.END_OF_DAY + 1;
        }
      }
    }

    // Add remaining time available after all events checked 
    if (availableStart + requestDuration <= TimeRange.END_OF_DAY) {
      possibleMeetingTimes.add(TimeRange.fromStartEnd(availableStart, TimeRange.END_OF_DAY, true));
    }
    return possibleMeetingTimes;
  }

  // Checks if any people in meeting request are in the event 
  public boolean checkOverlap(Collection<String> requestedAttendees, Collection<String> eventAttendees) {
    return !Collections.disjoint(requestedAttendees, eventAttendees);
  }
}
