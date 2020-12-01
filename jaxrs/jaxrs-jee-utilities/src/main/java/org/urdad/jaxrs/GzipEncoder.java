package org.urdad.jaxrs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

@Provider
@Priority(Priorities.ENTITY_CODER)
public class GzipEncoder implements WriterInterceptor
{

	public static class EndableGZIPOutputStream extends GZIPOutputStream
	{
		public EndableGZIPOutputStream(OutputStream outputStream) throws IOException
		{
			super(outputStream);
		}

		@Override
		public void finish() throws IOException
		{
			super.finish();
			// Make sure on finish the deflater's end() is called to release the native code pointer.
			def.end();
		}
	}

	public static class CommittedGZIPOutputStream extends CommitHeaderOutputStream
	{
		protected CommittedGZIPOutputStream(OutputStream delegate, CommitCallback headers)
		{
			super(delegate, headers);
		}

		public GZIPOutputStream getGzip()
		{
			return gzip;
		}

		@Override
		public void commit()
		{
			if (isHeadersCommitted)
			{
				return;
			}
			isHeadersCommitted = true;

			try
			{
				// GZIPOutputStream constructor writes to underlying OS causing headers to be written.
				// so we swap gzip OS in when we are ready to write.
				gzip = new EndableGZIPOutputStream(delegate);
				delegate = gzip;
			} catch (IOException e)
			{
				throw new RuntimeException(e);
			}

		}

		protected GZIPOutputStream gzip;

	}

	@Override
	public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException
	{
		Object acceptEncoding = httpHeaders.getRequestHeaders().getFirst(HttpHeaders.ACCEPT_ENCODING);

		if ((acceptEncoding != null) && (acceptEncoding.toString().equalsIgnoreCase("gzip")))
		{
			OutputStream outputStream = context.getOutputStream();

			// GZIPOutputStream constructor writes to underlying OS causing headers to be written.
			CommittedGZIPOutputStream gzipOutputStream = new CommittedGZIPOutputStream(outputStream, null);

			// Any content length set will be obsolete.
			context.getHeaders().remove("Content-Length");
			context.getHeaders().putSingle(HttpHeaders.CONTENT_ENCODING, "gzip");

			context.setOutputStream(gzipOutputStream);

			try
			{
				context.proceed();
			} finally
			{
				if (gzipOutputStream.getGzip() != null)
				{
					gzipOutputStream.getGzip().finish();
				}

				context.setOutputStream(outputStream);
			}

		}
		else
		{
			context.proceed();
		}
	}

	@Context
	private HttpHeaders httpHeaders;

}