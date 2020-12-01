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

package org.urdad.xml.binding;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.validation.constraints.NotEmpty;
import org.urdad.services.PreconditionViolation;
import org.urdad.services.Request;
import org.urdad.services.RequestNotValidException;
import org.urdad.services.Response;

/**
 * Service contracts that are associated with the conversion of Java objects to
 * XML.
 */
public interface Marshaller {

	/** FIXME: Javadoc */
	MarshallJavaObjectToXmlResponse marshallJavaObjectToXml(
			MarshallJavaObjectToXmlRequest marshallJavaObjectToXmlRequest)
			throws RequestNotValidException, JavaObjectOrBoundClassesNotValidException;

	/** FIXME: Javadoc. */
	@XmlRootElement
	@XmlType
	@XmlAccessorType(XmlAccessType.PROPERTY)
	class MarshallJavaObjectToXmlRequest extends Request {

		/** Default constructor. */
		public MarshallJavaObjectToXmlRequest() {
		}

		/** Convenience constructor. */
		public MarshallJavaObjectToXmlRequest(Object javaObject, Class... classesToBeBound) {
			this.javaObject = javaObject;
			this.classesToBeBound = classesToBeBound;
		}

		/** Convenience constructor. */
		public MarshallJavaObjectToXmlRequest(Object javaObject, String characterEncoding, Class... classesToBeBound) {
			this.javaObject = javaObject;
			this.setCharacterEncoding(characterEncoding);
			this.classesToBeBound = classesToBeBound;
		}

		/** Convenience constructor. */
		public MarshallJavaObjectToXmlRequest(Object javaObject, Boolean formatXml, Class... classesToBeBound) {
			this.javaObject = javaObject;
			this.classesToBeBound = classesToBeBound;
			this.formatXml = formatXml;
		}

		/** Convenience constructor. */
		public MarshallJavaObjectToXmlRequest(Object javaObject, Boolean formatXml, String characterEncoding,
				Class... classesToBeBound) {
			this.javaObject = javaObject;
			this.setCharacterEncoding(characterEncoding);
			this.classesToBeBound = classesToBeBound;
			this.formatXml = formatXml;
		}

		/** FIXME: Javadoc */
		public Object getJavaObject() {
			return javaObject;
		}

		public void setJavaObject(Object javaObject) {
			this.javaObject = javaObject;
		}

		/** FIXME: Javadoc */
		public Class[] getClassesToBeBound() {
			return classesToBeBound;
		}

		public void setClassesToBeBound(Class... classesToBeBound) {
			this.classesToBeBound = classesToBeBound;
		}

		/** FIXME: Javadoc */
		public Boolean getFormatXml() {
			return formatXml;
		}

		public void setFormatXml(Boolean formatXml) {
			this.formatXml = formatXml;
		}
		
		/** FIXME: Javadoc */
		public String getCharacterEncoding() {
			return characterEncoding;
		}

		public void setCharacterEncoding(String characterEncoding) {
			this.characterEncoding = characterEncoding;
		}

		@NotNull(message = "A Java object must be specified.")
		private Object javaObject;
		@NotNull(message = "The classes to be bound must be specified.")
		private Class[] classesToBeBound;
		private Boolean formatXml = false;

		@NotEmpty(message = "A character encoding must be specified.")
		private String characterEncoding = "UTF-8";
	}

	/** FIXME: Javadoc. */
	@XmlRootElement
	@XmlType
	@XmlAccessorType(XmlAccessType.PROPERTY)
	class MarshallJavaObjectToXmlResponse extends Response {

		/** Default constructor. */
		public MarshallJavaObjectToXmlResponse() {
		}

		/** Convenience constructor. */
		public MarshallJavaObjectToXmlResponse(String xml) {
			this.xml = xml;
		}

		/** FIXME: Javadoc */
		public String getXml() {
			return xml;
		}

		public void setXml(String xml) {
			this.xml = xml;
		}

		@NotEmpty(message = "XML must be specified.")
		private String xml;

	}

	/**
	 * Thrown to indicate that the 'Java object and bound class(es) combination
	 * must be valid' pre-condition has been violated.
	 */
	class JavaObjectOrBoundClassesNotValidException extends PreconditionViolation {

		/** Default constructor. */
		public JavaObjectOrBoundClassesNotValidException() {
		}

		/**
		 * Constructs a new
		 * <code>JavaObjectOrBoundClassesNotValidException</code> exception with
		 * the specified detail message.
		 *
		 * @param message
		 *            the detail message.
		 */
		public JavaObjectOrBoundClassesNotValidException(String message) {
			super(message);
		}

		/**
		 * Constructs a new
		 * <code>JavaObjectOrBoundClassesNotValidException</code> exception with
		 * the specified detail message and cause.
		 *
		 * @param message
		 *            the detail message.
		 * @param cause
		 *            the cause.
		 */
		public JavaObjectOrBoundClassesNotValidException(String message, Throwable cause) {
			super(message, cause);
		}

		/**
		 * Constructs a new
		 * <code>JavaObjectOrBoundClassesNotValidException</code> exception with
		 * the specified cause.
		 *
		 * @param cause
		 *            the cause.
		 */
		public JavaObjectOrBoundClassesNotValidException(Throwable cause) {
			super(cause);
		}

	}

	/** FIXME: Javadoc. */
	interface MarshallerLocal extends Marshaller{}
	/** FIXME: Javadoc. */
	interface MarshallerRemote extends Marshaller{}

}