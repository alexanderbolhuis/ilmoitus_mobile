//
//  DeclarationTypeController.m
//  Ilmoitus
//
//  Created by Administrator on 23/05/14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import "constants.h"
#import "DeclarationTypeController.h"
#import "DeclarationMainTypes.h"
#import "DeclarationSubTypes.h"

@interface DeclarationTypeController()

@property (strong, nonatomic) NSMutableArray *mainTypes;
@property (strong, nonatomic) NSMutableArray *subTypes;

@end

@implementation DeclarationTypeController

- (void)DownLoadMainTypes
{
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFHTTPRequestSerializer serializer];
    [manager.requestSerializer setValue:[[NSUserDefaults standardUserDefaults] stringForKey:@"token"] forHTTPHeaderField:@"Authorization"];
    NSString *url = [NSString stringWithFormat:@"%@/declarationtypes", baseURL];
    [manager GET:url parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject)
     {
         NSError* error;
         NSDictionary* json = [NSJSONSerialization
                               JSONObjectWithData:responseObject
                               
                               options:kNilOptions
                               error:&error];
         
         NSMutableArray *declarationsTypesFound = [[NSMutableArray alloc] init];
         for (NSDictionary *decl in json)
         {
             DeclarationMainTypes *declarationMainTypes = [[DeclarationMainTypes alloc] init];
             *declarationMainTypes.MainTypeId = [decl[@"id"] intValue];
             declarationMainTypes.MainTypeDescription = [decl[@"declarationType"] stringValue];
             [declarationsTypesFound addObject:declarationMainTypes];
         }
         [_mainTypes removeAllObjects];
         _mainTypes = declarationsTypesFound;
         
         NSLog(@"GET request success response for all declarations: %@", json);
     }
         failure:^(AFHTTPRequestOperation *operation, NSError *error)
     {
         NSLog(@"GET request Error for all declarations main types: %@", error);
     }];
}

// expect the id form the DeclarationMainType
- (void)DownLoadSubTypes:(NSInteger*)mainTpyeId
{
    NSString *combinedURL = [NSString stringWithFormat:@"%@%@", baseURL, @"/declarationsubtype/"];
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFHTTPRequestSerializer serializer];
    [manager.requestSerializer setValue:[[NSUserDefaults standardUserDefaults] stringForKey:@"token"] forHTTPHeaderField:@"Authorization"];
    NSString *url = [NSString stringWithFormat:[NSString stringWithFormat:@"%i", *mainTpyeId], combinedURL];
    [manager GET:url parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject)
    {
        NSError* error;
        NSDictionary* json = [NSJSONSerialization
                              JSONObjectWithData:responseObject
                              
                              options:kNilOptions
                              error:&error];
        
        NSMutableArray *declarationsTypesFound = [[NSMutableArray alloc] init];
        for (NSDictionary *decl in json)
        {
            DeclarationSubTypes *declarationSubTypes = [[DeclarationSubTypes alloc] init];
            *declarationSubTypes.SubTypeId = [decl[@"id"] intValue];
            declarationSubTypes.SubTypeDescription = [decl[@"declarationType"] stringValue];
            [declarationsTypesFound addObject:declarationSubTypes];
        }
        [_subTypes removeAllObjects];
        _subTypes = declarationsTypesFound;
        
        NSLog(@"GET request success response for all declarations sub types: %@", json);
    }
         failure:^(AFHTTPRequestOperation *operation, NSError *error)
    {
        NSLog(@"GET request Error for all declarations: %@", error);
    }];
}

- (NSMutableArray*)GetMainTypes
{
    return _mainTypes;
}
- (NSMutableArray*)GetSubType
{
    return _subTypes;
}

@end
