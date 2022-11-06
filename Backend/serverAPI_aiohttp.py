import asyncio
import glob
import logging
import os

import socketio
from aiohttp import web
from engineio.async_drivers import aiohttp

from src.normalizer.querynormalizer import QueryNormalizer
from src.normalizer.socket_documentnormalizer import SocketDocumentNormalizer
from src.searcher.socket_docuboolsearcher import SocketDocuBooleanSearcher
from src.searcher.socket_sentboolsearcher import SocketSentBooleanSearcher
from src.highlight import highlighter

# create a Socket.IO server
sio = socketio.AsyncServer(async_mode='aiohttp', async_handlers=True, cors_allowed_origins="https://amritb.github.io")

app = web.Application()
sio.attach(app)

clients = []


@sio.event
async def connect(sid, environ, auth):
    print(f'{sid}, Client connect')
    clients.append(sid)
    await sio.send(data={'data': 'Connected'})


@sio.event
async def disconnect(sid):
    print(f'{sid}, Client disconnected')
    clients.remove(sid)
    await sio.send(data={'data': 'Disconnected'})


@sio.on('test')
def test(sid, data):
    print(data)
    print(type(data))
    print(data['path'])


@sio.on('search_document')
async def document_search(sid, data):
    print(data)
    print("Retrieval documents list...")
    documents = glob.glob(os.path.join(data['path'], "*.{}".format('pdf')))
    print("Query Processing...")
    query = QueryNormalizer(data['keywords']).get_query()
    searcher = SocketDocuBooleanSearcher(query=query)
    print("Document Searching...")
    for document in documents:
        await asyncio.sleep(0.01)
        if sid not in clients:
            break
        print("Searching", document)
        index = SocketDocumentNormalizer(document).get_index()
        valid, result = searcher.search(document, index)
        if "T" in valid:
            print(document, "Match")
            await sio.emit('document_search', result)
    await sio.emit('end_searching', {"Message": "searching completed"})
    print("End of Searching")


@sio.on('search_sentence')
async def sentence_search(sid, data):
    print(data)
    print("Retrieval documents list...")
    documents = glob.glob(os.path.join(data['path'], "*.{}".format('pdf')))
    print("Query Processing...")
    query = QueryNormalizer(data['keywords']).get_query()
    searcher = SocketSentBooleanSearcher(query=query)
    print("Sentence Searching...")
    for document in documents:
        await asyncio.sleep(0.01)
        if sid not in clients:
            break
        print("Searching", document)
        index = SocketDocumentNormalizer(document).get_index()
        valid, result = searcher.search(document, index)
        if len(valid) != 0:
            print(document, "Match")
            await sio.emit('sentence_search', result)
    await sio.emit('end_searching', {"Message": "searching completed"})
    print("End of Searching")


@sio.on('highlight')
async def highlight(sid, data):
    print(data)
    highlighter.highlight(path=data['path'],
                          list_coordinates=data['list_coordinates'])
    print("End of highlighting...")
    await sio.emit('highlight', {"Message": "highlight complete"})


if __name__ == "__main__":
    if not os.path.exists("log"):
        os.mkdir("log")
    logging.basicConfig(filename="log/search_engine.log", level=logging.DEBUG)
    web.run_app(app, host="127.0.0.1", port="8000")
