FROM python:3.12.11-slim
RUN pip install --no-cache-dir pymodbus==3.11.3
COPY tools/pymodbus_testbed.py /opt/atlas/pymodbus_testbed.py
EXPOSE 502
ENTRYPOINT ["python", "-u", "/opt/atlas/pymodbus_testbed.py"]
