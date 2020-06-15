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
import java.util.Collection;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    //throw new UnsupportedOperationException("TODO: Implement this method.");
    //Collection<TimeRange> mandatory_attendees = request.getAttendees();
    //Collection<TimeRange> optional_attendees = request.getOptionalAttendees();

    return findTime(events, request);
  }

  public Collection<TimeRange> findTime(Collection<Event> events, MeetingRequest request) {
    
    List<TimeRange> possibleMeetingTimes = new ArrayList<>();
    int requestDuration = (int)request.getDuration();
    Collection<String> requestAttendees = request.getAttendees();
    int numEvents = events.size();
    int availableStart = TimeRange.START_OF_DAY;
    int event_start;
    int event_end;
    boolean skip;
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
    
    for (Event e : events) {
      counter++;
      skip = true;
      eventAttendees = e.getAttendees();

      // Ignore event if no overlapping attendees in event and requested meeting
      for (String attendee: eventAttendees) {
        if (requestAttendees.contains(attendee)) {
          skip = false;
          break;
        } else if (counter == numEvents) {
          possibleMeetingTimes.add(TimeRange.fromStartEnd(availableStart, TimeRange.END_OF_DAY, true));
        }
      }

      // Adjust available meeting times based on event start/end 
      if (!skip) {
        event_start = e.getWhen().start();
        event_end = e.getWhen().end();

        if (event_start == TimeRange.START_OF_DAY) {
          availableStart = event_end;
        }

        // Check overlapping events 
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
          }
        }
      }
    }
    return possibleMeetingTimes;
  }
}
