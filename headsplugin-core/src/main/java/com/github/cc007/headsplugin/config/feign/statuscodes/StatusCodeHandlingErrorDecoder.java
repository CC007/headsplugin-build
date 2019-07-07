package com.github.cc007.headsplugin.config.feign.statuscodes;

import feign.Response;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Component;

@Component
public class StatusCodeHandlingErrorDecoder implements ErrorDecoder
{
	private final Decoder decoder;

	private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

	private final Map<ErrorDecoderKey, Class> decoderKeyActions = new HashMap<>();

	public StatusCodeHandlingErrorDecoder(Decoder decoder)
	{
		this.decoder = decoder;
	}

	@Override
	public Exception decode(String methodKey, Response response)
	{
		AtomicReference<Exception> result = new AtomicReference<>();
		result.set(defaultErrorDecoder.decode(methodKey, response));

		decoderKeyActions.entrySet().stream()
			.filter((entry) -> {
				ErrorDecoderKey errorDecoderKey = entry.getKey();
				return methodKey.equals(errorDecoderKey.getMethodKey()) && response.status() == errorDecoderKey.getStatusCode();
			})
			.findFirst()
			.ifPresent((entry) -> {
				try {
					Object obj = decoder.decode(response, entry.getValue());
					result.set(new ErrorDecoderException(obj, obj.getClass()));
				}
				catch (IOException e) {
					result.set(e);
				}
			});

		return result.get();
	}

	public void addKey(ErrorDecoderKey key, Class aClass)
	{
		if (!decoderKeyActions.containsKey(key)) {
			decoderKeyActions.put(key, aClass);
		}
	}
}