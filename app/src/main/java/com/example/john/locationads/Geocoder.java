package com.example.john.locationads;


import android.location.Location;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Geocoder
{
    public static String reverseGeocode(Location loc)
    {
        //http://maps.google.com/maps/geo?q=40.714224,-73.961452&output=json&oe=utf8&sensor=true_or_false&key=your_api_key
        String localityName = "";
        HttpURLConnection connection = null;
        URL serverAddress = null;

        try
        {
            // build the URL using the latitude & longitude you want to lookup
            // NOTE: I chose XML return format here but you can choose something else
            serverAddress = new URL("http://maps.google.com/maps/geo?q=" + Double.toString(loc.getLatitude()) + "," + Double.toString(loc.getLongitude()) +
                    "&output=xml&oe=utf8&sensor=true&key=AIzaSyDm1YfyFLens7Hfxz403I7kd0hhy5D2wLo");
            //set up out communications stuff
            connection = null;

            //Set up the initial connection
            connection = (HttpURLConnection)serverAddress.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setReadTimeout(10000);

            connection.connect();

            try
            {
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                InputSource source = new InputSource(isr);
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                XMLReader xr = parser.getXMLReader();
                GoogleReverseGeocodeXmlHandler handler = new GoogleReverseGeocodeXmlHandler();

                xr.setContentHandler(handler);
                xr.parse(source);

                localityName = handler.getLocalityName();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return localityName;
    }
}

class GoogleReverseGeocodeXmlHandler extends DefaultHandler
{
    private boolean inLocalityName = false;
    private boolean finished = false;
    private StringBuilder builder;
    private String localityName;

    public String getLocalityName()
    {
        return this.localityName;
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        if (this.inLocalityName && !this.finished)
        {
            if ((ch[start] != '\n') && (ch[start] != ' '))
            {
                builder.append(ch, start, length);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException
    {
        super.endElement(uri, localName, name);

        if (!this.finished)
        {
            if (localName.equalsIgnoreCase("LocalityName"))
            {
                this.localityName = builder.toString();
                this.finished = true;
            }

            if (builder != null)
            {
                builder.setLength(0);
            }
        }
    }

    @Override
    public void startDocument() throws SAXException
    {
        super.startDocument();
        builder = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
    {
        super.startElement(uri, localName, name, attributes);

        if (localName.equalsIgnoreCase("LocalityName"))
        {
            this.inLocalityName = true;
        }
    }
}