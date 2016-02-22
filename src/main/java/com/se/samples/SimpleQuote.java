/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.se.samples;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class SimpleQuote extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean bSymbol = false;
        String symbolName = "WSO2";
        XMLStreamWriter xMLStreamWriter = null;

        resp.addHeader("Content-Type", "application/xml");
        PrintWriter writer = resp.getWriter();

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader eventReader = xmlInputFactory.createXMLEventReader(req.getInputStream());

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                switch (event.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    StartElement startElement = event.asStartElement();
                    String qName = startElement.getName().getLocalPart();
                    if ("symbol".equals(qName)) {
                        bSymbol = true;
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    Characters characters = event.asCharacters();
                    if (bSymbol) {
                        symbolName = characters.toString();
                        break;
                    }
                    break;
                }
            }

            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
            xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(writer);

            xMLStreamWriter.writeStartElement("response");
            xMLStreamWriter.writeStartElement("symbol");
            xMLStreamWriter.writeCharacters(symbolName);
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeStartElement("quote");
            xMLStreamWriter.writeCharacters(Double.toString(Math.random() * 100));
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeEndElement();

            writer.flush();

        } catch (XMLStreamException e) {
            e.printStackTrace();
        } finally {
            writer.close();
            try {
                if (xMLStreamWriter != null) {
                    xMLStreamWriter.close();
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

    }

}
