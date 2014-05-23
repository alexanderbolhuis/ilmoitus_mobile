//
//  DeclarationTypeController.h
//  Ilmoitus
//
//  Created by Administrator on 23/05/14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface DeclarationTypeController : NSObject

- (void)DownLoadMainTypes;
- (void)DownLoadSubTypes:(NSInteger*)mainTpyeId;

- (NSMutableArray*)GetMainTypes;
- (NSMutableArray*)GetSubType;

@end
