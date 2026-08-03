FROM python:3.9-alpine
ADD . /code
WORKDIR /code
RUN pip install --trusted-host pypi.org \
                --trusted-host files.pythonhosted.org \
                --trusted-host pypi.python.org \
                -r requirements.txt
EXPOSE 5000
CMD ["python", "app.py"]