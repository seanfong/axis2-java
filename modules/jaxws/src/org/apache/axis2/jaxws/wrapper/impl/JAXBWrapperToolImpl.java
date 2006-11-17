/*
 * Copyright 2004,2005 The Apache Software Foundation.
 * Copyright 2006 International Business Machines Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axis2.jaxws.wrapper.impl;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElement;

import org.apache.axis2.jaxws.i18n.Messages;
import org.apache.axis2.jaxws.util.XMLRootElementUtil;
import org.apache.axis2.jaxws.wrapper.JAXBWrapperTool;


public class JAXBWrapperToolImpl implements JAXBWrapperTool {

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.wrapped.JAXBWrapperTool#unWrap(java.lang.Object, javax.xml.bind.JAXBContext, java.util.ArrayList)
	 */
	
	/*
	 * create property descriptor using jaxbObject and child Names,
	 * getReader and read the object, form the object array and return them.
	 */
	
	public Object[] unWrap(Object jaxbObject, 
			List<String> childNames) throws JAXBWrapperException{
		try{
			if(jaxbObject == null){
				throw new JAXBWrapperException(Messages.getMessage("JAXBWrapperErr1"));
			}
			if(childNames == null){
				throw new JAXBWrapperException(Messages.getMessage("JAXBWrapperErr2"));
			}
            
            // Review: I think that we can remove the next statement.  This is an assertion of the tool
            // Get the object that will have the property descriptors (i.e. the object representing the complexType)
            Object jaxbComplexTypeObj = (jaxbObject instanceof JAXBElement) ?
                    ((JAXBElement)jaxbObject).getValue() : // Type object is the value of the JAXBElement
                        jaxbObject;                        // Or JAXBObject represents both the element and anon complexType
            
			ArrayList<Object> objList = new ArrayList<Object>();
			Map<String , PropertyInfo> pdTable = createPropertyDescriptors(jaxbComplexTypeObj.getClass(), childNames);
			for(String childName:childNames){
				PropertyInfo propInfo = pdTable.get(childName);
				Object object = propInfo.get(jaxbComplexTypeObj);
				objList.add(object);
			}
			Object[] jaxbObjects = objList.toArray();
			objList = null;
			return jaxbObjects;
		}catch(IntrospectionException e){
			throw new JAXBWrapperException(e);
		}catch(IllegalAccessException e){
			throw new JAXBWrapperException(e);
		}catch(InvocationTargetException e){
			throw new JAXBWrapperException(e);
		}catch(NoSuchFieldException e){
			throw new JAXBWrapperException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.axis2.jaxws.wrapped.JAXBWrapperTool#wrap(java.lang.Class, java.lang.String, java.util.ArrayList, java.util.ArrayList)
	 */
	public Object wrap(Class jaxbClass, 
			List<String> childNames, Map<String, Object> childObjects)
			throws JAXBWrapperException {
		
		try{
			if(childNames == null|| childObjects == null){
				throw new JAXBWrapperException(Messages.getMessage("JAXBWrapperErr3"));
			}
			if(childNames.size() != childObjects.size()){
				throw new JAXBWrapperException(Messages.getMessage("JAXBWrapperErr4"));
			}
			Map<String, PropertyInfo> pdTable = createPropertyDescriptors(jaxbClass, childNames);
			Object jaxbObject = jaxbClass.newInstance();
			for(String childName:childNames){
				PropertyInfo propInfo = pdTable.get(childName);
				propInfo.set(jaxbObject, childObjects.get(childName));
			}
			return jaxbObject;
		}catch(IntrospectionException e){
			throw new JAXBWrapperException(e);
		}catch(InstantiationException e){
			throw new JAXBWrapperException(e);
		}catch(IllegalAccessException e){
			throw new JAXBWrapperException(e);
		}catch(InvocationTargetException e){
			throw new JAXBWrapperException(e);
		}catch(NoSuchFieldException e){
			throw new JAXBWrapperException(e);
		}
	}
	
	/** creates propertyDescriptor for the childNames using the jaxbClass.  
	 * use Introspector.getBeanInfo().getPropertyDescriptors() to get all the property descriptors. Assert if # of childNames and propertyDescriptor array
	 * length do not match. if they match then get the xmlElement name from jaxbClass using propertyDescriptor's display name. See if the xmlElementName matches the 
	 * childName if not use xmlElement annotation name and create PropertyInfo add childName or xmlElement name there, set propertyDescriptor 
	 * and return Map<ChileName, PropertyInfo>.
	 * @param jaxbClass - Class jaxbClass name
	 * @param childNames - ArrayList<String> of xml childNames 
	 * @return Map<String, PropertyInfo> - map of ChildNames that map to PropertyInfo that hold the propertyName and PropertyDescriptor.
	 * @throws IntrospectionException, NoSuchFieldException
	 */
	private Map<String, PropertyInfo> createPropertyDescriptors(Class jaxbClass, List<String> childNames) throws IntrospectionException, NoSuchFieldException, JAXBWrapperException{
		Map<String, PropertyInfo> map = new WeakHashMap<String, PropertyInfo>();
		
		
		Map<String, PropertyDescriptor>  pdMap = XMLRootElementUtil.createPropertyDescriptorMap(jaxbClass);
		Field field[] = jaxbClass.getDeclaredFields();
        
        // It is possible the that the number of fields is greater than the number of child elements due
        // to customizations.
		if(field.length < childNames.size()){
			throw new JAXBWrapperException(Messages.getMessage("JAXBWrapperErr4", jaxbClass.getName()));
		}
        
		
        // Create property infos for each class name
        for (int i=0; i<childNames.size(); i++) {
            PropertyInfo propInfo= null;
            String childName = childNames.get(i);
            PropertyDescriptor pd = pdMap.get(childName);
            if(pd == null){
                throw new JAXBWrapperException(Messages.getMessage("JAXBWrapperErr6", jaxbClass.getName(), childName));
            }
            propInfo = new PropertyInfo(pd);
            map.put(childName, propInfo);
        }
        
		
		return map;
	}
	
	
	
}
