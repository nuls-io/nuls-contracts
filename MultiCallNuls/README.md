## NULS MultiCall

### Main class

`MultiCallNuls.java`

### Core Query Functions

- aggregate
    - Performs batch queries. Each contract’s query result is returned as a string, with the final output being an array of strings.
    - Parameters: (String[] contracts, String[] methods, String[] args)
    - return: String[]
    - If any contract query fails, the function refuses to execute.
- aggregateStrict
    - Performs batch queries. Each contract’s query result is returned as a string, with the final output being an array of strings.
    - Parameters: (String[] contracts, String[] methods, String[] args, boolean strict)
    - return: String[]
    - If the `strict` parameter is set to `false` and a contract query fails, that contract returns an empty string `""`

### Explanation of args in Core Query Functions

> Assume there are three contract queries:

- Query without parameters:
    - If a query has no input parameters, use an empty string "" for that contract.
    - Example: The first contract query has no input parameters, args = ["", "abc", "abc"].
- Query with a single parameter:
    - If a query has one input parameter, place the parameter value directly in the corresponding position.
    - Example: The first contract query has one input parameter, args = ["aa", "abc", "abc"].
- Query with multiple parameters:
    - If a query has two or more input parameters, separate the parameter values with commas `,`.
    - Example: The first contract query has three input parameters, args = ["aa,bb,cc", "abc", "abc"].
- Query with array parameters:
    - If a query’s input parameter is an array, separate the array elements with colons `:`.
    - Example: The first contract query has an array as its input parameter, args = ["aa:bb:cc", "abc", "abc"].
- Query with mixed parameters:
    - If a query has multiple input parameters, including arrays, separate the standard parameters with commas `,`, and array parameters with colons `:`.
    - Example: The first contract query has three input parameters, with the second being an array, args = ["aa,bob:123:alice,cc", "abc", "abc"].

### Test data on the test network

- (String[] contracts, String[] methods, String[] args)

    - ["tNULSeBaMwP81fGuNPRRSpKxLto1o1hQPnUTJQ","tNULSeBaMwEvSUS6UZixK3ycdmZSrbEr9f21Uj","tNULSeBaMwEvSUS6UZixK3ycdmZSrbEr9f21Uj","tNULSeBaN6kswC7TKzCtDjYKUiuCBZS7rRR4aJ","tNULSeBaN6kswC7TKzCtDjYKUiuCBZS7rRR4aJ","tNULSeBaMwEvSUS6UZixK3ycdmZSrbEr9f21Uj","tNULSeBaN6kswC7TKzCtDjYKUiuCBZS7rRR4aJ", "tNULSeBaN6kswC7TKzCtDjYKUiuCBZS7rRR4aJ"]
    - ["userTokenBalancesAndNulsAvailable","getLastAward","getDomainPrice","name","symbol","userDomains","ownerOf","ownerOf"]
        - userTokenBalancesAndNulsAvailable(String user, String[] nrc20s) return String[]
        - getLastAward() return String
        - getDomainPrice(String suffix, int length) return String
        - name() return String
        - symbol() return String
        - userDomains(Address user) return Map
        - ownerOf(BigInteger tokenId) return Address
        - ownerOf(BigInteger tokenId) return Address
    - ["tNULSeBaMvEtDfvZuukDf2mVyfGo3DdiN8KLRG,tNULSeBaN8Ps39De43Gik5GfQ6h4GYsHGmwNcP:tNULSeBaN3uYkVi8bY7qUsTrQSYLbgBuZvfmcN:tNULSeBaMyCzpjWfk4Y4EfGaQq9z72C12GLRpy::tNULSeBaMxCJQwsXpY3xfo6nwY3k8tRacs9pBH","","nuls,2","","","tNULSeBaMqrByvidryVtysy1WKEkDPZTeazymf","0","1"]

    - Result
        - `["[\"2621995600000\",\"360000000\",\"9000000\",\"38591740726975\",\"3000000\"]","2098411403","500000000000","nulsDomain","NULSDOMAIN","{\"mainDomain\":\"zz.nuls\",\"uri\":null,\"pub\":null,\"activeDomains\":[\"zz.nuls\"],\"inactiveDomains\":[],\"received\":\"0\",\"pending\":\"0\",\"rewardDebt\":\"0\"}","tNULSeBaMoKW8Ktk6pDt4HeravsQXxMXXxFQy8","tNULSeBaMqrByvidryVtysy1WKEkDPZTeazymf"]
`

### Multicall Contract Address

> Multicall Address On The Testnet: tNULSeBaMwP81fGuNPRRSpKxLto1o1hQPnUTJQ
>
> Multicall Address On The Mainnet: NULSd6Hgrnv1oxcdyhzZmsu7HWgk7vcaR6nMR

### Contract address updated on March 24, 2025 at 22:20:15

> Multicall Address On The Testnet: tNULSeBaNBvS52PkAubbRDeKPqwB75VQntorBY
>
> Multicall Address On The Mainnet: NULSd6HgtpqAdEGKLqRZ2H1sxDT6eGbGFJRJg

_Added two methods to query the balances of multiple NRC20 assets or cross-chain assets of one user_

- userTokenBalancesAndAssetAvailable (String user, String[] nrc20s) return String[]
  - Available balance of NRC20 assets and available balance of cross-chain assets
- userTokenBalancesAndAssetTotal (String user, String[] nrc20s) return String[]
  - Available balance of NRC20 assets and total balance of cross-chain assets

