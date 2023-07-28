package com.tcs.sgv.integration.service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ArthwahiniMapCoverter implements Converter{
	public ArthwahiniMapCoverter() {

	}

	public boolean canConvert(Class type) {

		return AbstractMap.class.isAssignableFrom(type);
	}

	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {

		AbstractMap map = (AbstractMap) value;
		for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); writer.endNode()) {
			Object obj = iterator.next();
			java.util.Map.Entry entry = (java.util.Map.Entry) obj;
			writer.startNode(entry.getKey().toString());
			context.convertAnother(entry.getValue());
		}

	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

		List<Map> lLstVoucherDtls = new ArrayList<Map>();
		Map innerMap = null;
		for (; reader.hasMoreChildren(); reader.moveUp()) {
			reader.moveDown();
			HierarchicalStreamReader childReader = reader.underlyingReader();
			innerMap = new HashMap();
			for (; childReader.hasMoreChildren(); childReader.moveUp()) {
				childReader.moveDown();
				innerMap.put(childReader.getNodeName(), childReader.getValue());
			}
			lLstVoucherDtls.add(innerMap);
		}
		return lLstVoucherDtls;
	}
}
