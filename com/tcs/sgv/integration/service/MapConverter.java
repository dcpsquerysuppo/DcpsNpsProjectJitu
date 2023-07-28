package com.tcs.sgv.integration.service;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


public class MapConverter implements Converter {

	public MapConverter() {

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

		Map map = new HashMap();
		for (; reader.hasMoreChildren(); reader.moveUp()) {
			reader.moveDown();
			if (reader.getNodeName().endsWith("InnerMap")) {
				Map innerMap = (Map) context.convertAnother(map, Map.class);
				map.put(reader.getNodeName(), innerMap);
			} else if (reader.getNodeName().endsWith("InnerList")) {
				List innerList = (List) context.convertAnother(map, List.class);
				map.put(reader.getNodeName(), innerList);
			} else {
				map.put(reader.getNodeName(), reader.getValue());
			}
		}

		return map;
	}
}