/*
 * Copyright 2019 Dr. Fritz Solms & Craig Edwards
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.urdad.xml.binding.adapter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * FIXME: Javadoc.
 */
public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    public LocalDate unmarshal(String string) throws Exception {
        try {
            return LocalDate.parse(string);
        } catch (DateTimeParseException e) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(string)), ZoneId.systemDefault()).toLocalDate();
        }
    }

    public String marshal(LocalDate localDate) throws Exception {
        return localDate.toString();
    }

}