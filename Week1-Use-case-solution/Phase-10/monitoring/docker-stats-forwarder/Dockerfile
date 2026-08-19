FROM docker:27-cli

RUN apk add --no-cache jq curl bash

COPY stats-forwarder.sh /stats-forwarder.sh
RUN chmod +x /stats-forwarder.sh

ENTRYPOINT ["/stats-forwarder.sh"]
