package org.urdad.jaxrs;

import java.io.IOException;
import java.io.OutputStream;

public class CommitHeaderOutputStream extends OutputStream
{
	public CommitHeaderOutputStream(OutputStream delegate, CommitCallback headers)
	{
		this.delegate = delegate;
		this.headers = headers;
	}

	public interface CommitCallback
	{
		void commit();
	}

	public void commit()
	{
		if (isHeadersCommitted)
		{
			return;
		}
		isHeadersCommitted = true;
		headers.commit();
	}

	@Override
	public void write(int i) throws IOException
	{
		commit();
		delegate.write(i);
	}

	@Override
	public void write(byte[] bytes) throws IOException
	{
		commit();
		delegate.write(bytes);
	}

	@Override
	public void write(byte[] bytes, int i, int i1) throws IOException
	{
		commit();
		delegate.write(bytes, i, i1);
	}

	@Override
	public void flush() throws IOException
	{
		commit();
		delegate.flush();
	}

	@Override
	public void close() throws IOException
	{
		commit();
		delegate.close();
	}

	protected OutputStream delegate;
	protected boolean isHeadersCommitted;
	protected CommitCallback headers;
}