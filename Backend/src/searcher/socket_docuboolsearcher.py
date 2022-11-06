from . import booleansearcher
from .booleansearcher import operationFun
from .searcher import Searcher


class SocketDocuBooleanSearcher(Searcher):

    def __init__(self, query):
        self.__query = query

    def search(self, document, index):
        match_words = list()
        valid = set("T")
        for keyword in self.__query:
            tem_valid = set()
            posting = index.get_post(keyword['keyword'])
            if posting is not None:
                match = False
                for word in posting.get_words():
                    #if word.get_pos() == keyword['pos'] or "ANY" == keyword['pos']:
                    if word.get_pos() in keyword['pos']:
                        match_words.append(word)
                        match = True
                if match:
                    tem_valid.add("T")
            valid = getattr(booleansearcher, operationFun.get(keyword['operator']))(valid, tem_valid)

        results = {"MatchDocument": document, "Words": [word.__dict__ for word in match_words]}
        return valid, results
