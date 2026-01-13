package domain.document;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Document {
    TypeDocument typeDocument;
    String value;
}
