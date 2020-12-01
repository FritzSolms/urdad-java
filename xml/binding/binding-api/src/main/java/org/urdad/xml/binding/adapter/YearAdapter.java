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

import java.time.Year;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * FIXME: Javadoc.
 */
public class YearAdapter extends XmlAdapter<String, Year>
{

    public Year unmarshal(String string) throws Exception
    {
        return Year.parse(string);
    }

    public String marshal(Year year) throws Exception
    {
        return year.toString();
    }

}