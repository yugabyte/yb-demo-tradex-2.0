FROM python:3.9.16-slim-bullseye
# Or any preferred Python version.
RUN apt-get update
RUN apt-get install -y wget curl python3-pip
RUN pip install yfinance==0.2.4 psycopg2-binary --upgrade --no-cache-dir
RUN mkdir /trade-data-loader
ADD generateSymbolHistoryV2.py /trade-data-loader
RUN chmod +x /trade-data-loader/*.py
WORKDIR /trade-data-loader
CMD ["python3", "./generateSymbolHistoryV2.py"]
# Or enter the name of your unique directory and parameter set.