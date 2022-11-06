import logging

operationFun = {"AND": "intercept", "OR": "union", "NOT": "difference"}


def intercept(results, tem_doc):
    logging.debug(f"intercept results set {results}, tem set {tem_doc}")
    results = results.intersection(tem_doc)
    return results


def union(results, tem_doc):
    logging.debug(f"union results set {results}, tem set {tem_doc}")
    results = results.union(tem_doc)
    return results


def difference(results, tem_doc):
    logging.debug(f"difference results set {results}, tem set {tem_doc}")
    results = results.difference(tem_doc)
    return results
